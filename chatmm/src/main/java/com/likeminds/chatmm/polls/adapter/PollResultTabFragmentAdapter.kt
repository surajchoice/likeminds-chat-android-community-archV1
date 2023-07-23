package com.likeminds.chatmm.polls.adapter

import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.polls.adapter.databinders.PollResultUserItemViewDataBinder
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class PollResultTabFragmentAdapter constructor(
    val userPreferences: UserPreferences,
    val adapterListener: PollResultTabFragmentInterface,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val pollResultUserItemViewDataBinder =
            PollResultUserItemViewDataBinder(userPreferences, adapterListener)
        viewDataBinders.add(pollResultUserItemViewDataBinder)

        return viewDataBinders
    }
}

interface PollResultTabFragmentInterface {
    fun isMemberOfCommunity(): Boolean
    fun isEditable(): Boolean
    fun showMemberProfile(memberViewData: MemberViewData, source: String)
    fun showMemberOptionsDialog(memberViewData: MemberViewData)
}