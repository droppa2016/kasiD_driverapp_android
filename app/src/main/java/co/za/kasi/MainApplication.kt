package co.za.kasi

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import co.za.kasi.services.activityTracker.InactivityHandler
import co.za.kasi.utils.ReusableFunctions
import co.za.kasi.utils.ReusableMethods
import com.onesignal.OneSignal.initWithContext


class MainApplication : Application(), Application.ActivityLifecycleCallbacks{

    private lateinit var inactivityHandler: InactivityHandler
    var currentActivity: Activity? = null
        private set
    override fun onCreate() {
        super.onCreate()
        instance = this
        initWithContext(this, ReusableFunctions.getOneSignalID(this@MainApplication))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        registerActivityLifecycleCallbacks(this)
        inactivityHandler = InactivityHandler()
        inactivityHandler.start((2 * 60 * 60 * 1000).toLong()) {
            Log.e("inactivity", "User inactive for 2 hours. Logging out.")
         ReusableMethods.performLightSignOut(this@MainApplication,currentActivity)
        }

    }

    private fun injectTouchListener(activity: Activity) {
        val originalCallback = activity.window.callback
        activity.window.callback = object : Window.Callback by originalCallback {
            override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                inactivityHandler.resetTimer()
                return originalCallback.dispatchTouchEvent(event)
            }
        }
    }

    private fun updateLastInteraction(context: Context) {
        getSharedPreferences("inactivity", Context.MODE_PRIVATE)
            .edit()
            .putLong("last_interaction", System.currentTimeMillis())
            .apply()
    }

    companion object {
        @get:Synchronized
        var instance: MainApplication? = null
            private set
    }


    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
       injectTouchListener(activity = activity)
    }
    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        inactivityHandler.resetTimer()
    }

    override fun onActivityPaused(activity: Activity) {
        inactivityHandler.stopTimer()
        updateLastInteraction(this@MainApplication)
        if (currentActivity == activity) currentActivity = null
    }

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}


}
