package co.za.kasi.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import co.za.kasi.AccountAccessActivity
import co.za.kasi.services.LocalStorage
import co.za.kasi.services.location.LocationService1

object ReusableMethods {

    fun performLightSignOut(context : Context,currentActivity: Activity?){
        val serviceIntent = Intent(context, LocationService1::class.java)
        serviceIntent.setAction(LocationService1.ACTION_STOP)
        context.startService(serviceIntent)
       LocalStorage.clearLoginDate()
        currentActivity?.let {
            AccountAccessActivity.launchAccountAccessActivity(it)
            it.finish()
        } ?: Log.e("Logout", "No foreground activity to launch login screen.")
    }

    fun performHardSignOut(context : Context,currentActivity: Activity?){
        LocalStorage.storeSkynetDriverAccount(null)
        LocalStorage.storeSelectedVehicle(null)
        LocalStorage.clearCachedCredentials()
         performLightSignOut(context,currentActivity)
    }
}