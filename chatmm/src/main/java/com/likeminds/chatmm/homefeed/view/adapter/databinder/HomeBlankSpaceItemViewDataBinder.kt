package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemHomeBlankSpaceBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_BLANK_SPACE_VIEW

class HomeBlankSpaceItemViewDataBinder :
    ViewDataBinder<ItemHomeBlankSpaceBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_HOME_BLANK_SPACE_VIEW

    override fun createBinder(parent: ViewGroup): ItemHomeBlankSpaceBinding {
        return ItemHomeBlankSpaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    // todo: check if space width is required
    override fun bindData(
        binding: ItemHomeBlankSpaceBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}