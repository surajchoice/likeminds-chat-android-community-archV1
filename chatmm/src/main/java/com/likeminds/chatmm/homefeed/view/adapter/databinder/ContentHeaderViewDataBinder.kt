package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemContentHeaderBinding
import com.likeminds.chatmm.homefeed.model.ContentHeaderViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_CONTENT_HEADER_VIEW
import javax.inject.Inject

class ContentHeaderViewDataBinder @Inject constructor() :
    ViewDataBinder<ItemContentHeaderBinding, ContentHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_CONTENT_HEADER_VIEW

    override fun createBinder(parent: ViewGroup): ItemContentHeaderBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemContentHeaderBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemContentHeaderBinding,
        data: ContentHeaderViewData,
        position: Int
    ) {
        binding.apply {
            tvHeaderTitle.text = data.title
        }
    }
}