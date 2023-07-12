package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val TYPE_NORMAL = 0
const val TYPE_ANNOUNCEMENT = 7
const val TYPE_DIRECT_MESSAGE = 10

@IntDef(
    TYPE_NORMAL,
    TYPE_ANNOUNCEMENT,
    TYPE_DIRECT_MESSAGE
)
@Retention(AnnotationRetention.SOURCE)
annotation class ChatroomType {
    companion object {

        fun contains(state: Int?): Boolean {
            return state == TYPE_NORMAL ||
                    state == TYPE_ANNOUNCEMENT ||
                    state == TYPE_DIRECT_MESSAGE
        }

        fun isAnnouncementRoom(type: Int?): Boolean {
            return type == TYPE_ANNOUNCEMENT
        }

        fun isDMRoom(type: Int?): Boolean {
            return type == TYPE_DIRECT_MESSAGE
        }
    }
}
