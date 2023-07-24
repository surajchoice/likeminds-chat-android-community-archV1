package com.likeminds.chatmm.search.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemSingleShimmerBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SINGLE_SHIMMER

class SingleShimmerViewDataBinder :
    ViewDataBinder<ItemSingleShimmerBinding, BaseViewType>() {
    override val viewType: Int
        get() = ITEM_SINGLE_SHIMMER

    override fun createBinder(parent: ViewGroup): ItemSingleShimmerBinding {
        return ItemSingleShimmerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemSingleShimmerBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}