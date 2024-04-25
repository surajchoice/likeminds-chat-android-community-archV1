package com.likeminds.chatmm.dm.view.adapter.databinder


import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.LayoutEmptyDmFeedBinding
import com.likeminds.chatmm.dm.model.DMFeedEmptyViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_DM_FEED_EMPTY_DATA

class EmptyDMFeedViewDataBinder :
    ViewDataBinder<LayoutEmptyDmFeedBinding, DMFeedEmptyViewData>() {

    override val viewType: Int
        get() = ITEM_DM_FEED_EMPTY_DATA

    override fun createBinder(parent: ViewGroup): LayoutEmptyDmFeedBinding {
        return LayoutEmptyDmFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LayoutEmptyDmFeedBinding,
        data: DMFeedEmptyViewData,
        position: Int
    ) {
    }
}