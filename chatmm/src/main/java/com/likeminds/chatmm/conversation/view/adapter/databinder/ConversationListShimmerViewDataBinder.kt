package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemConversationListShimmerBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_LIST_SHIMMER_VIEW
import javax.inject.Inject

internal class ConversationListShimmerViewDataBinder @Inject constructor() :
    ViewDataBinder<ItemConversationListShimmerBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_LIST_SHIMMER_VIEW

    override fun createBinder(parent: ViewGroup): ItemConversationListShimmerBinding {
        return ItemConversationListShimmerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemConversationListShimmerBinding,
        data: BaseViewType,
        position: Int,
    ) {
    }
}