package co.za.kasi.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import co.za.kasi.exceptions.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response

class OfflineAPIInterceptor(
    private val context : Context
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        if(!isNetworkAvailable()){
            val request = chain.request()

            throw NoNetworkException("No network available. Request saved for later.")
        }

        return chain.proceed(chain.request())

    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}