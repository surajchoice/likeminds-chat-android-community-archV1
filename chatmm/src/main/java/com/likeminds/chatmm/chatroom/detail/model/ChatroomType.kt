package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val TYPE_NORMAL = 0
const val TYPE_INTRO = 1
const val TYPE_EVENT = 2
const val TYPE_POLL = 3
const val TYPE_HIDDEN = 4
const val TYPE_UN_VERIFY = 5
const val TYPE_EVENT_PUBLIC = 6
const val TYPE_ANNOUNCEMENT = 7
const val TYPE_DATE = 8
const val TYPE_INTRODUCTION = 9
const val TYPE_DIRECT_MESSAGE = 10

@IntDef(
    TYPE_NORMAL,
    TYPE_INTRO,
    TYPE_EVENT,
    TYPE_POLL,
    TYPE_HIDDEN,
    TYPE_UN_VERIFY,
    TYPE_EVENT_PUBLIC,
    TYPE_ANNOUNCEMENT,
    TYPE_DATE,
    TYPE_INTRODUCTION,
    TYPE_DIRECT_MESSAGE
)
@Retention(AnnotationRetention.SOURCE)
annotation class ChatroomType {
    companion object {

        fun contains(state: Int?): Boolean {
            return state == TYPE_NORMAL ||
                    state == TYPE_INTRO ||
                    state == TYPE_EVENT ||
                    state == TYPE_POLL ||
                    state == TYPE_HIDDEN ||
                    state == TYPE_UN_VERIFY ||
                    state == TYPE_EVENT_PUBLIC ||
                    state == TYPE_ANNOUNCEMENT ||
                    state == TYPE_DATE ||
                    state == TYPE_INTRODUCTION ||
                    state == TYPE_DIRECT_MESSAGE
        }

        fun isAnnouncementRoom(type: Int?): Boolean {
            return type == TYPE_ANNOUNCEMENT
        }

        fun isIntroductionRoom(type: Int?): Boolean {
            return type == TYPE_INTRODUCTION
        }

        fun isSingleIntroRoom(type: Int?): Boolean {
            return type == TYPE_INTRO
        }

        fun isEventChatRoom(type: Int?): Boolean {
            return type == TYPE_EVENT || type == TYPE_EVENT_PUBLIC
        }

        fun isDmRoom(type: Int?): Boolean {
            return type == TYPE_DIRECT_MESSAGE
        }
    }
}
