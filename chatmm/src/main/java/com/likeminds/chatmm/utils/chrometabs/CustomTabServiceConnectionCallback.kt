package com.likeminds.chatmm.utils.chrometabs

import androidx.browser.customtabs.CustomTabsClient

internal interface CustomTabServiceConnectionCallback {
    fun onServiceConnected(client: CustomTabsClient?)
    fun onServiceDisconnected()
}