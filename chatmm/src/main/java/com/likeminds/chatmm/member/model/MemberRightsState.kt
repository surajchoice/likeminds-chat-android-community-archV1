package com.likeminds.chatmm.member.model

import androidx.annotation.IntDef

const val MEMBER_RIGHT_CREATE_ROOMS = 0
const val MEMBER_RIGHT_CREATE_POLL = 1
const val MEMBER_RIGHT_CREATE_EVENT = 2
const val MEMBER_RIGHT_RESPOND_IN_ROOM = 3
const val MEMBER_RIGHT_INVITE_PRIVATE_LINK = 4
const val MEMBER_RIGHT_AUTO_APPROVE = 5
const val MEMBER_RIGHT_CREATE_SECRET_ROOMS = 6
const val MEMBER_RIGHT_DIRECT_MESSAGES = 7

//this right is -1 as it is handled on the client side and in future we can have state as 7 for any other rights from backend
const val MEMBER_RIGHT_CONTENT_DOWNLOAD_SETTINGS = -1

@IntDef(
    MEMBER_RIGHT_CREATE_ROOMS,
    MEMBER_RIGHT_CREATE_POLL,
    MEMBER_RIGHT_CREATE_EVENT,
    MEMBER_RIGHT_RESPOND_IN_ROOM,
    MEMBER_RIGHT_INVITE_PRIVATE_LINK,
    MEMBER_RIGHT_AUTO_APPROVE,
    MEMBER_RIGHT_CREATE_SECRET_ROOMS,
    MEMBER_RIGHT_CONTENT_DOWNLOAD_SETTINGS,
    MEMBER_RIGHT_DIRECT_MESSAGES
)
@Retention(AnnotationRetention.SOURCE)
annotation class MemberRightsState {
    companion object {
        fun isCreateNormalChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_CREATE_ROOMS
        }

        fun isCreateOtherTypeChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_CREATE_POLL
                    || memberRightState == MEMBER_RIGHT_CREATE_EVENT
                    || memberRightState == MEMBER_RIGHT_CREATE_SECRET_ROOMS
        }

        fun isCreateAnyTypeChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_CREATE_ROOMS
                    || memberRightState == MEMBER_RIGHT_CREATE_POLL
                    || memberRightState == MEMBER_RIGHT_CREATE_EVENT
                    || memberRightState == MEMBER_RIGHT_CREATE_SECRET_ROOMS
        }

        fun isRespondInChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_RESPOND_IN_ROOM
        }

        fun isAutoApproveChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_AUTO_APPROVE
        }

        fun isCreateSecretChatRoom(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_CREATE_SECRET_ROOMS
        }

        fun isContentDownloadSetting(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_CONTENT_DOWNLOAD_SETTINGS
        }

        fun isDirectMessage(memberRightState: Int?): Boolean {
            return memberRightState == MEMBER_RIGHT_DIRECT_MESSAGES
        }
    }
}