package com.likeminds.chatmm.community.utils

import com.likeminds.chatmm.community.model.CommunitySettingType
import com.likeminds.likemindschat.community.model.CommunitySetting

object LMChatCommunitySettingsUtil {
    private val communitySettings: MutableList<CommunitySetting> = mutableListOf()

    //function to set community settings
    fun setCommunitySettings(settings: List<CommunitySetting>) {
        this.communitySettings.apply {
            clear()
            addAll(settings)
        }
    }

    //function to get community settings
    fun getCommunitySettings(): List<CommunitySetting> {
        return communitySettings
    }

    //function to check if secret chatroom invite is enabled
    fun isSecretChatroomInviteEnabled(): Boolean {
        val communitySetting =
            communitySettings.find {
                it.settingType == CommunitySettingType.SECRET_CHATROOM_INVITE.value
            }
        return communitySetting?.enabled ?: false
    }

    //function to check if direct messaging is enabled
    fun isDirectMessagingEnabled(): Boolean {
        val communitySetting =
            communitySettings.find {
                it.settingType == CommunitySettingType.DIRECT_MESSAGING.value
            }
        return communitySetting?.enabled ?: false
    }
}