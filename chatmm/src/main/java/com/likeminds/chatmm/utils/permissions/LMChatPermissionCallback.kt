package com.likeminds.chatmm.utils.permissions

interface LMChatPermissionCallback {
    fun onGrant()
    fun onDeny()
}