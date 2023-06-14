package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemHomeLineBreakBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_LINE_BREAK_VIEW
import javax.inject.Inject

class HomeLineBreakViewDataBinder @Inject constructor() :
    ViewDataBinder<ItemHomeLineBreakBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_HOME_LINE_BREAK_VIEW

    override fun createBinder(parent: ViewGroup): ItemHomeLineBreakBinding {
        return ItemHomeLineBreakBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemHomeLineBreakBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}