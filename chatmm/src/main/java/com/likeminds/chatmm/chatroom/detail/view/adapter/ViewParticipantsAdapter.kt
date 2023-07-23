package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.view.adapter.databinder.ViewParticipantItemViewDataBinder
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ViewParticipantsAdapter constructor(
    val listener: ViewParticipantsAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)
        val viewParticipantItemViewDataBinder = ViewParticipantItemViewDataBinder(listener)
        viewDataBinders.add(viewParticipantItemViewDataBinder)

        return viewDataBinders
    }
}

interface ViewParticipantsAdapterListener {
    fun onMemberClick(memberViewData: MemberViewData) {}
}
