package com.likeminds.chatmm.utils.connectivity

interface ConnectivityReceiverListener {
    fun onNetworkConnectionChanged(isConnected: Boolean)
}