package com.likeminds.chatmm.media.view.adapter

import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.view.adapter.databinder.ImageSwipeItemViewDataBinder
import com.likeminds.chatmm.media.view.adapter.databinder.VideoSwipeItemViewDataBinder
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class MediaSwipeAdapter constructor(
    val listener: ImageSwipeAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(2)

        val imageSwipeItemViewDataBinder = ImageSwipeItemViewDataBinder(listener)
        viewDataBinders.add(imageSwipeItemViewDataBinder)

        val videoSwipeItemViewDataBinder = VideoSwipeItemViewDataBinder(listener)
        viewDataBinders.add(videoSwipeItemViewDataBinder)
        return viewDataBinders
    }
}

interface ImageSwipeAdapterListener {
    fun onImageClicked()
    fun onImageViewed() {}
    fun onVideoClicked(mediaSwipeViewData: MediaSwipeViewData)
}