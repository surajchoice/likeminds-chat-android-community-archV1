package com.likeminds.chatmm.chatroom.detail.view.adapter

import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.utils.ValueUtils.getItemInList
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ChatroomDetailAdapter constructor(
    val listener: ChatroomDetailAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {
    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(0)
        return viewDataBinders
    }

    operator fun get(position: Int): BaseViewType? {
        return items().getItemInList(position)
    }
}

interface ChatroomDetailAdapterListener {
    fun getChatRoom(): ChatroomViewData?
}