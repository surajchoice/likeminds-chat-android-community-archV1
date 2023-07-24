package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.view.adapter.databinder.ItemReactionViewDataBinder
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ReactionAdapter constructor(
    val userPreferences: UserPreferences,
    val listener: ReactionAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(1)

        val messageReactionViewDataBinder =
            ItemReactionViewDataBinder(userPreferences, listener)
        viewDataBinders.add(messageReactionViewDataBinder)
        return viewDataBinders
    }
}

interface ReactionAdapterListener {
    fun removeReaction(conversationId: String)
    fun removeChatroomReaction()
}
