package com.likeminds.chatmm.utils

import com.likeminds.chatmm.chatroom.detail.model.MemberViewData

object MemberUtil {

    fun getFirstNameToShow(
        sdkPreferences: SDKPreferences,
        memberViewData: MemberViewData?
    ): String {
        val memberID = memberViewData?.id
        return if (memberViewData == null) ""
        else if (sdkPreferences.getMemberId() == memberID) "You:"
        else {
            val name = memberViewData.name?.trim()?.split(" ")?.get(0)
            if (name != null) "$name:" else ""
        }
    }
}