package com.likeminds.chatmm.utils.permissions

interface PermissionCallback {
    fun onGrant()
    fun onDeny()
}