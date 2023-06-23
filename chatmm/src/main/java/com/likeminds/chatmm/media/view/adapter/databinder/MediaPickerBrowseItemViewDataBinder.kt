package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemMediaPickerBrowseBinding
import com.likeminds.chatmm.media.model.MediaBrowserViewData
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_BROWSE
import javax.inject.Inject

class MediaPickerBrowseItemViewDataBinder @Inject constructor(
    private val listener: MediaPickerAdapterListener,
) : ViewDataBinder<ItemMediaPickerBrowseBinding, MediaBrowserViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_BROWSE

    override fun createBinder(parent: ViewGroup): ItemMediaPickerBrowseBinding {
        val binding = ItemMediaPickerBrowseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            listener.browseDocumentClicked()
        }
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerBrowseBinding,
        data: MediaBrowserViewData,
        position: Int
    ) {
    }
}