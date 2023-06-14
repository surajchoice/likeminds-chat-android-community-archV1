package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.LayoutEmptyDataBinding
import com.likeminds.chatmm.homefeed.model.EmptyScreenViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_EMPTY_VIEW
import javax.inject.Inject

class EmptyViewDataBinder @Inject constructor() :
    ViewDataBinder<LayoutEmptyDataBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_EMPTY_VIEW

    override fun createBinder(parent: ViewGroup): LayoutEmptyDataBinding {
        return LayoutEmptyDataBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LayoutEmptyDataBinding,
        data: BaseViewType,
        position: Int
    ) {
        binding.apply {
            viewData = data as EmptyScreenViewData
            root.visibility = View.VISIBLE
        }
    }
}