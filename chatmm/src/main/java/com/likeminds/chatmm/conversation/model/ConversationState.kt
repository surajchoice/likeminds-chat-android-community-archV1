package com.likeminds.chatmm.conversation.model

import androidx.annotation.IntDef

const val STATE_NORMAL = 0
const val STATE_HEADER = 1
const val STATE_FOLLOWED = 2
const val STATE_UN_FOLLOWED = 3
const val STATE_EDIT_COMMUNITY_PURPOSE = 5
const val STATE_GUEST_FOLLOWED = 6
const val STATE_CHATROOM_ADD_PARTICIPANT = 7
const val STATE_LEAVE_CHATROOM = 8
const val STATE_REMOVED_FROM_CHATROOM = 9
const val STATE_POLL = 10
const val STATE_ADD_MEMBERS = 11
const val STATE_TOPIC = 12
const val DM_MEMBER_REMOVED_OR_LEFT = 13
const val DM_CM_BECOMES_MEMBER_DISABLE = 14
const val DM_MEMBER_BECOMES_CM = 15
const val DM_CM_BECOMES_MEMBER_ENABLE = 16
const val DM_MEMBER_BECOMES_CM_ENABLE = 17

@IntDef(
    STATE_NORMAL,
    STATE_HEADER,
    STATE_FOLLOWED,
    STATE_UN_FOLLOWED,
    STATE_EDIT_COMMUNITY_PURPOSE,
    STATE_GUEST_FOLLOWED,
    STATE_CHATROOM_ADD_PARTICIPANT,
    STATE_LEAVE_CHATROOM,
    STATE_REMOVED_FROM_CHATROOM,
    STATE_POLL,
    STATE_ADD_MEMBERS,
    STATE_TOPIC,
    DM_MEMBER_REMOVED_OR_LEFT,
    DM_CM_BECOMES_MEMBER_DISABLE,
    DM_MEMBER_BECOMES_CM,
    DM_CM_BECOMES_MEMBER_ENABLE,
    DM_MEMBER_BECOMES_CM_ENABLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class ConversationsState {

    companion object {
        fun contains(state: Int): Boolean {
            return state == STATE_NORMAL ||
                    state == STATE_HEADER ||
                    state == STATE_FOLLOWED ||
                    state == STATE_UN_FOLLOWED ||
                    state == STATE_EDIT_COMMUNITY_PURPOSE ||
                    state == STATE_GUEST_FOLLOWED ||
                    state == STATE_CHATROOM_ADD_PARTICIPANT ||
                    state == STATE_LEAVE_CHATROOM ||
                    state == STATE_REMOVED_FROM_CHATROOM ||
                    state == STATE_POLL ||
                    state == STATE_ADD_MEMBERS ||
                    state == STATE_TOPIC ||
                    state == DM_MEMBER_REMOVED_OR_LEFT ||
                    state == DM_CM_BECOMES_MEMBER_DISABLE ||
                    state == DM_MEMBER_BECOMES_CM ||
                    state == DM_CM_BECOMES_MEMBER_ENABLE ||
                    state == DM_MEMBER_BECOMES_CM_ENABLE
        }

        fun isPoll(state: Int): Boolean {
            return state == STATE_POLL
        }

        //return whether state is of poll (10) or normal(0)
        fun isPollOrNormal(state: Int): Boolean {
            return state == STATE_POLL || state == STATE_NORMAL
        }
    }
}

