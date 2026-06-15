package dev.korryr.shambaguard.ui.features.auth.view

object BiometricPromptBridge {

    interface Callback {
        fun onSuccess()
        fun onError(error: String)
    }

    @Volatile
    private var callback: Callback? = null

    fun setCallback(cb: Callback) {
        callback = cb
    }

    fun deliverSuccess() {
        try {
            callback?.onSuccess()
        } finally {
            clear()
        }
    }

    fun deliverError(err: String) {
        try {
            callback?.onError(err)
        } finally {
            clear()
        }
    }

    fun clear() {
        callback = null
    }
}
