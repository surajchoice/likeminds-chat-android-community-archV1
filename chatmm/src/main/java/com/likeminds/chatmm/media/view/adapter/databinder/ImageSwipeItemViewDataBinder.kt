package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemImageSwipeBinding
import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.view.adapter.ImageSwipeAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_IMAGE_SWIPE

class ImageSwipeItemViewDataBinder constructor(
    val listener: ImageSwipeAdapterListener
) : ViewDataBinder<ItemImageSwipeBinding, MediaSwipeViewData>() {

    override val viewType: Int
        get() = ITEM_IMAGE_SWIPE

    override fun createBinder(parent: ViewGroup): ItemImageSwipeBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageSwipeBinding.inflate(inflater, parent, false)
        binding.photoView.setOnClickListener {
            listener.onImageClicked()
        }
        return binding
    }

    override fun bindData(binding: ItemImageSwipeBinding, data: MediaSwipeViewData, position: Int) {
        binding.uri = data.uri.toString()
        listener.onImageViewed()
        binding.executePendingBindings()
    }

}