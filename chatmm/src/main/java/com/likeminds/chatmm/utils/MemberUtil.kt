package com.likeminds.chatmm.utils

import com.likeminds.chatmm.chatroom.detail.model.MemberViewData

object MemberUtil {

    fun getFirstNameToShow(
        userPreferences: UserPreferences,
        memberViewData: MemberViewData?
    ): String {
        val memberID = memberViewData?.id
        return if (memberViewData == null) ""
        else if (userPreferences.getMemberId() == memberID) "You:"
        else {
            val name = memberViewData.name?.trim()?.split(" ")?.get(0)
            if (name != null) "$name:" else ""
        }
    }
}