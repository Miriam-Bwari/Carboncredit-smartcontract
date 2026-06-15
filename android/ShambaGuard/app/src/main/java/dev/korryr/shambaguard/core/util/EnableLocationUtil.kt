package dev.korryr.shambaguard.core.util

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task

/**
 * Checks if device location (GPS) is turned on.
 * If not, it displays the standard Google Play Services dialog asking the user to turn it on.
 */
@Composable
fun EnableLocationEffect(
    onLocationEnabled: () -> Unit,
    onLocationDeclined: () -> Unit,
) {
    val context = LocalContext.current

    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            // User agreed to turn on GPS
            onLocationEnabled()
        } else {
            // User denied or dismissed the dialog
            onLocationDeclined()
        }
    }

    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true) // Show the dialog even if "never ask again" was previously checked (if applicable)

        val client = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS is already turned on
            onLocationEnabled()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // GPS is off, but we can prompt the user to turn it on
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    settingResultRequest.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    // Ignore the error
                    onLocationDeclined()
                }
            } else {
                // GPS is off and can't be resolved programmatically
                onLocationDeclined()
            }
        }
    }
}
