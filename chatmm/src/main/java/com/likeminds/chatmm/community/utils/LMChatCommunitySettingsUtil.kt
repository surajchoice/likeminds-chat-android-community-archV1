package com.likeminds.chatmm.community.utils

import com.likeminds.chatmm.community.model.CommunitySettingType
import com.likeminds.likemindschat.community.model.CommunitySetting

object LMChatCommunitySettingsUtil {
    private val communitySettings: MutableList<CommunitySetting> = mutableListOf()

    fun setCommunitySettings(settings: List<CommunitySetting>) {
        this.communitySettings.apply {
            clear()
            addAll(settings)
        }
    }

    fun getCommunitySettings(): List<CommunitySetting> {
        return communitySettings
    }

    fun isSecretChatroomInviteEnabled(): Boolean {
        val communitySetting =
            communitySettings.find {
                it.settingType == CommunitySettingType.SECRET_CHATROOM_INVITE.value
            }
        return communitySetting?.enabled ?: false
    }
}