package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val STATE_NOTHING = 0
const val STATE_ADMIN = 1
const val STATE_TEMP_ADMIN = 2
const val STATE_PENDING = 3
const val STATE_MEMBER = 4
const val STATE_DECLINED = 5
const val STATE_ACCEPT_INVITATION = 6
const val STATE_MEMBER_ACCEPT_INVITATION = 7
const val STATE_INTERESTED_MEMBER = 8
const val STATE_SKIP_PRIVATE_LINK = 9

@IntDef(
    STATE_NOTHING,
    STATE_ADMIN,
    STATE_TEMP_ADMIN,
    STATE_PENDING,
    STATE_MEMBER,
    STATE_DECLINED,
    STATE_ACCEPT_INVITATION,
    STATE_MEMBER_ACCEPT_INVITATION,
    STATE_INTERESTED_MEMBER,
    STATE_SKIP_PRIVATE_LINK
)
@Retention(AnnotationRetention.SOURCE)
annotation class MemberState {
    companion object {
        fun isPendingMember(memberState: Int?): Boolean {
            return memberState == STATE_PENDING
        }

        fun isAdmin(memberState: Int?): Boolean {
            return memberState == STATE_ADMIN || memberState == STATE_TEMP_ADMIN
        }

        fun isNothing(memberState: Int?): Boolean {
            return memberState == STATE_NOTHING
        }

        fun isMember(memberState: Int?): Boolean {
            return memberState == STATE_MEMBER
        }

        fun isMemberSkipPrivateLink(memberState: Int?): Boolean {
            return memberState == STATE_SKIP_PRIVATE_LINK
        }

        fun isGuest(memberState: Int?) =
            memberState == null || memberState == STATE_NOTHING || memberState == STATE_PENDING

        fun getMemberState(memberState: Int?): String {
            return when {
                isAdmin(memberState) -> "admin"
                isMember(memberState) -> "member"
                isMemberSkipPrivateLink(memberState) -> "skipped_member"
                isPendingMember(memberState) -> "pending_member"
                else -> "guest"
            }

        }
    }
}