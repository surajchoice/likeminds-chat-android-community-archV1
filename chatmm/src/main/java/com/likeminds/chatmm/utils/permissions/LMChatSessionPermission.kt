package com.likeminds.chatmm.utils.permissions

import android.app.Application
import com.likeminds.chatmm.utils.sharedpreferences.BasePreferences
import javax.inject.Inject

class LMChatSessionPermission @Inject constructor(application: Application) :
    BasePreferences(PERMISSION_PREFS, application) {
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
