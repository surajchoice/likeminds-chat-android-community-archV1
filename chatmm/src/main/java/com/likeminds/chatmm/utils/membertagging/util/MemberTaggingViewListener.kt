package com.likeminds.chatmm.utils.membertagging.util

import com.likeminds.chatmm.utils.membertagging.model.TagViewData

interface MemberTaggingViewListener {

    fun onMemberTagged(user: TagViewData) {}

    fun onMemberRemoved(user: TagViewData) {}

    fun onShow() {}

    fun onHide() {}

    fun callApi(page: Int, searchName: String) {} //call tagging api
}