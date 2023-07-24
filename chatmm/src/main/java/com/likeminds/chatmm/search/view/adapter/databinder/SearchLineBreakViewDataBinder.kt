package com.likeminds.chatmm.search.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemLineBreakSearchBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_LINE_BREAK_VIEW

class SearchLineBreakViewDataBinder :
    ViewDataBinder<ItemLineBreakSearchBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_SEARCH_LINE_BREAK_VIEW

    override fun createBinder(parent: ViewGroup): ItemLineBreakSearchBinding {
        return ItemLineBreakSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemLineBreakSearchBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}