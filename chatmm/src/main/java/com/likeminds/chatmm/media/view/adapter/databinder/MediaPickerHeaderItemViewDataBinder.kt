package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemMediaPickerHeaderBinding
import com.likeminds.chatmm.media.model.MediaHeaderViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_HEADER

class MediaPickerHeaderItemViewDataBinder :
    ViewDataBinder<ItemMediaPickerHeaderBinding, MediaHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_HEADER

    override fun createBinder(parent: ViewGroup): ItemMediaPickerHeaderBinding {
        return ItemMediaPickerHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    override fun bindData(
        binding: ItemMediaPickerHeaderBinding,
        data: MediaHeaderViewData,
        position: Int
    ) {
        binding.tvHeader.text = data.title
    }
}