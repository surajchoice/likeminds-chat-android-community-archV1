package com.likeminds.chatmm.utils.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

open class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    private var connectivityReceiverListener: ConnectivityReceiverListener? = null

    override fun onReceive(context: Context, intent: Intent) {
        connectivityReceiverListener?.onNetworkConnectionChanged(checkInternet(context))
    }

    fun setListener(connectivityReceiverListener: ConnectivityReceiverListener?) {
        this.connectivityReceiverListener = connectivityReceiverListener
    }

    private fun checkInternet(context: Context): Boolean {
        var result = false
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val capabilities =
                    manager.getNetworkCapabilities(manager.activeNetwork) // need ACCESS_NETWORK_STATE permission
                result =
                    capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        } else {
            manager.run {
                manager.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }
}
