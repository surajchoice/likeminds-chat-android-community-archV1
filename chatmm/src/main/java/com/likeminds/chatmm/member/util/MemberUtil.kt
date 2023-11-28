package com.likeminds.chatmm.member.util

import com.likeminds.chatmm.member.model.MemberViewData

object MemberUtil {

    // gets the first name of the member to show
    fun getFirstNameToShow(
        userPreferences: UserPreferences,
        memberViewData: MemberViewData?
    ): String {
        val memberUUID = memberViewData?.sdkClientInfo?.uuid
        return if (memberViewData == null) ""
        else if (userPreferences.getUUID() == memberUUID) {
            "You:"
        } else {
            val name = memberViewData.name?.trim()?.split(" ")?.get(0)
            if (name != null) "$name:" else ""
        }
    }

    // gets the name of the member to be displayed
    fun getMemberNameForDisplay(
        member: MemberViewData,
        currentMemberId: String
    ): String {
        return if (currentMemberId == member.sdkClientInfo.uuid) {
            "You"
        } else {
            member.name ?: ""
        }
    }
}