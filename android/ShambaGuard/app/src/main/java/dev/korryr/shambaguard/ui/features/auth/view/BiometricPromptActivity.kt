package dev.korryr.shambaguard.ui.features.auth.view

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import timber.log.Timber
import dev.korryr.shambaguard.R

/**
 * Transient activity that shows BiometricPrompt and returns result via BiometricPromptBridge.
 * This ensures compatibility with BiometricPrompt (which requires a FragmentActivity) while
 * allowing the main app to remain a ComponentActivity. It also handles Android 9 specific edge cases.
 */
class BiometricPromptActivity : FragmentActivity() {

    private var biometricPrompt: BiometricPrompt? = null
    private var isAuthenticationCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val context = this
            val executor = ContextCompat.getMainExecutor(context)

            biometricPrompt = BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (!isAuthenticationCompleted) {
                            isAuthenticationCompleted = true
                            Timber.d("BiometricPromptActivity: success")
                            BiometricPromptBridge.deliverSuccess()
                            finish()
                        }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (!isAuthenticationCompleted) {
                            isAuthenticationCompleted = true
                            Timber.w("BiometricPromptActivity: error $errorCode - $errString")

                            when (errorCode) {
                                BiometricPrompt.ERROR_USER_CANCELED,
                                BiometricPrompt.ERROR_CANCELED -> {
                                    BiometricPromptBridge.deliverError("") // Silent cancel
                                }
                                else -> {
                                    BiometricPromptBridge.deliverError(errString.toString())
                                }
                            }
                            finish()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Timber.w("BiometricPromptActivity: failed")
                        // Do not finish; allow the user to retry
                    }
                }
            )

            val bm = BiometricManager.from(context)
            val requestedForNewer = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            val fallbackNegativeButton = context.getString(R.string.cancel)

            var chosenFlags: Int? = null
            val tryCases = listOf(
                requestedForNewer to "STRONG|DEVICE",
                BiometricManager.Authenticators.BIOMETRIC_STRONG to "STRONG",
                (BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL) to "WEAK|DEVICE",
                BiometricManager.Authenticators.BIOMETRIC_WEAK to "WEAK"
            )

            for ((flags, label) in tryCases) {
                try {
                    val r = bm.canAuthenticate(flags)
                    if (r == BiometricManager.BIOMETRIC_SUCCESS) {
                        chosenFlags = flags
                        break
                    }
                } catch (e: Exception) {
                    Timber.w(e, "canAuthenticate(\$label) threw")
                }
            }

            val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_unlock))
                .setSubtitle(context.getString(R.string.biometric_unlock_subtitle))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && chosenFlags != null) {
                try {
                    promptInfoBuilder.setAllowedAuthenticators(chosenFlags)
                } catch (e: IllegalArgumentException) {
                    promptInfoBuilder.setNegativeButtonText(fallbackNegativeButton)
                }
            } else {
                promptInfoBuilder.setNegativeButtonText(fallbackNegativeButton)
            }

            val promptInfo = try {
                promptInfoBuilder.build()
            } catch (e: IllegalArgumentException) {
                BiometricPrompt.PromptInfo.Builder()
                    .setTitle(context.getString(R.string.biometric_unlock))
                    .setSubtitle(context.getString(R.string.biometric_unlock_subtitle))
                    .setNegativeButtonText(fallbackNegativeButton)
                    .build()
            }

            biometricPrompt?.authenticate(promptInfo)
        } catch (e: Exception) {
            Timber.e(e, "BiometricPromptActivity failed to create prompt")
            BiometricPromptBridge.deliverError("Biometric not supported: ${e.message}")
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isAuthenticationCompleted && isFinishing) {
            biometricPrompt?.cancelAuthentication()
        }
        BiometricPromptBridge.clear()
    }
}
