package com.likeminds.chatmm.member.view.adapter

import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.member.view.adapter.databinder.DMAllMembersViewDataBinder
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import javax.inject.Inject

class DMAllMemberAdapter @Inject constructor(
    private val listener: DMAllMemberAdapterListener,
    private val userPreferences: UserPreferences
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val dmAllMembersViewDataBinder = DMAllMembersViewDataBinder(
            listener,
            userPreferences
        )

        viewDataBinders.add(dmAllMembersViewDataBinder)
        return viewDataBinders
    }
}

interface DMAllMemberAdapterListener {
    fun onMemberSelected(member: MemberViewData)
}