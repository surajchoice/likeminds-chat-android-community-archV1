package com.likeminds.chatmm.utils.chrometabs

import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection

internal class CustomTabServiceConnection(private val connectionCallback: CustomTabServiceConnectionCallback?) :
    CustomTabsServiceConnection() {

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        this.connectionCallback?.onServiceConnected(client)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        this.connectionCallback?.onServiceDisconnected()
    }

}