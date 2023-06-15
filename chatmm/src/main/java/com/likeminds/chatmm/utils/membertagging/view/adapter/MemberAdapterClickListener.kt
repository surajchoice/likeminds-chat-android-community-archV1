package com.likeminds.chatmm.utils.membertagging.view.adapter

import com.likeminds.chatmm.utils.membertagging.model.TagViewData

internal interface MemberAdapterClickListener {
    fun onMemberTagged(user: TagViewData)
}