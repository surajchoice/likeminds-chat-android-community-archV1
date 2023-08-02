package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemVideoSwipeBinding
import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.view.adapter.ImageSwipeAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.ITEM_VIDEO_SWIPE

class VideoSwipeItemViewDataBinder constructor(
    val listener: ImageSwipeAdapterListener
) : ViewDataBinder<ItemVideoSwipeBinding, MediaSwipeViewData>() {

    override val viewType: Int
        get() = ITEM_VIDEO_SWIPE

    override fun createBinder(parent: ViewGroup): ItemVideoSwipeBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoSwipeBinding.inflate(inflater, parent, false)
        binding.ivVideoPlaceholder.setOnClickListener {
            val mediaSwipeViewData = binding.mediaSwipeViewData ?: return@setOnClickListener
            listener.onVideoClicked(mediaSwipeViewData)
        }
        return binding
    }

    override fun bindData(
        binding: ItemVideoSwipeBinding,
        data: MediaSwipeViewData,
        position: Int
    ) {
        binding.apply {
            mediaSwipeViewData = data
            if (!data.thumbnail.isNullOrEmpty()) {
                ImageBindingUtil.loadImage(
                    ivVideoPlaceholder,
                    data.thumbnail
                )
            } else {
                ImageBindingUtil.loadImage(
                    ivVideoPlaceholder,
                    data.thumbnail
                )
            }
            binding.executePendingBindings()
        }
    }
}