package com.likeminds.chatmm.search.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemContentHeaderSearchBinding
import com.likeminds.chatmm.search.model.SearchContentHeaderViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_CONTENT_HEADER_VIEW

class SearchContentHeaderViewDataBinder :
    ViewDataBinder<ItemContentHeaderSearchBinding, SearchContentHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_SEARCH_CONTENT_HEADER_VIEW

    override fun createBinder(parent: ViewGroup): ItemContentHeaderSearchBinding {
        return ItemContentHeaderSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemContentHeaderSearchBinding,
        data: SearchContentHeaderViewData,
        position: Int
    ) {
        binding.title = data.title
    }
}