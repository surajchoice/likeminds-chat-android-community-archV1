package com.likeminds.chatmm.chatroom.detail.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDateViewData
import com.likeminds.chatmm.databinding.ItemConversationActionBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_CHATROOM_DATE
import javax.inject.Inject

class ChatroomDateItemViewDataBinder @Inject constructor() :
    ViewDataBinder<ItemConversationActionBinding, ChatroomDateViewData>() {

    override val viewType: Int
        get() = ITEM_CHATROOM_DATE

    override fun createBinder(parent: ViewGroup): ItemConversationActionBinding {
        val inflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(
            inflater,
            R.layout.item_conversation_action,
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemConversationActionBinding,
        data: ChatroomDateViewData,
        position: Int,
    ) {
        val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = 0
        binding.tvAction.text = data.date
    }
}