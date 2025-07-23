package co.za.kasi.services.activityTracker

import android.os.Handler
import android.os.Looper

class InactivityHandler {
    private val handler = Handler(Looper.getMainLooper())
    private var logoutRunnable: Runnable? = null
    private var timeoutMillis: Long = 0
    private var logoutCallback: (() -> Unit)? = null

    fun start(timeout: Long, callback: () -> Unit) {
        timeoutMillis = timeout
        logoutCallback = callback
        resetTimer()
    }
    fun resetTimer() {
        stopTimer()
        logoutRunnable = Runnable {
            logoutCallback?.invoke()
        }
        handler.postDelayed(logoutRunnable!!, timeoutMillis)
    }

    fun stopTimer() {
        logoutRunnable?.let {
            handler.removeCallbacks(it)
        }
    }
}