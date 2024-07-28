package com.likeminds.chatmm.utils.permissions

import android.app.Application
import android.content.Context
import com.likeminds.chatmm.utils.sharedpreferences.BasePreferences
import javax.inject.Inject

class LMChatSessionPermission @Inject constructor(context: Context) :
    BasePreferences(PERMISSION_PREFS, context) {
    companion object {
        const val PERMISSION_PREFS = "permission_prefs"
    }

    fun setPermissionRequest(permissionName: String) {
        putPreference(permissionName, true)
    }

    fun wasPermissionRequestedBefore(permissionName: String): Boolean {
        return getPreference(permissionName, false)
    }
}
