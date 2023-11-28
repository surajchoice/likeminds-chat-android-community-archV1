package com.likeminds.chatmm.member.view.adapter

import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.member.view.adapter.databinder.CommunityMembersViewDataBinder
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import javax.inject.Inject

class CommunityMembersAdapter @Inject constructor(
    private val listener: CommunityMembersAdapterListener,
    private val userPreferences: UserPreferences
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val communityMembersViewDataBinder = CommunityMembersViewDataBinder(
            listener,
            userPreferences
        )

        viewDataBinders.add(communityMembersViewDataBinder)
        return viewDataBinders
    }
}

interface CommunityMembersAdapterListener {
    fun onMemberSelected(member: MemberViewData)
}