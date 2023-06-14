package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemHomeChatroomListShimmerBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CHATROOM_LIST_SHIMMER_VIEW
import javax.inject.Inject

class HomeChatroomListShimmerViewDataBinder @Inject constructor() :
    ViewDataBinder<ItemHomeChatroomListShimmerBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CHATROOM_LIST_SHIMMER_VIEW

    override fun createBinder(parent: ViewGroup): ItemHomeChatroomListShimmerBinding {
        return ItemHomeChatroomListShimmerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemHomeChatroomListShimmerBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}