package com.likeminds.chatmm.member.util

import com.likeminds.chatmm.member.model.MemberViewData

object MemberUtil {

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

    // todo:
    //creates subtitle as per designation and community name
//    fun createSubtitle(memberViewData: MemberViewData): String {
//        val basicQuestions = memberViewData.listOfQuestionAnswerViewData?.filter {
//            it.tag == QuestionTag.Basic.value
//        } ?: return ""
//
//        val communityName = basicQuestions.find {
//            it.state == STATE_SHORT_ANSWER
//        }?.value ?: return ""
//
//        val designation = basicQuestions.find {
//            it.state == STATE_MULTIPLE_CHOICE_SINGLE
//        }?.value ?: return ""
//
//        return ("$designation @ $communityName")
//    }
}