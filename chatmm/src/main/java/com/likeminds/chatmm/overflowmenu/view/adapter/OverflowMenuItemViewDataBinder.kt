package com.likeminds.chatmm.overflowmenu.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.ItemOverflowMenuBinding
import com.likeminds.chatmm.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_OVERFLOW_MENU_ITEM

class OverflowMenuItemViewDataBinder(private val overflowMenuAdapterListener: OverflowMenuAdapterListener) :
    ViewDataBinder<ItemOverflowMenuBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    override fun createBinder(parent: ViewGroup): ItemOverflowMenuBinding {
        val binding =
            ItemOverflowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener {
            val data = binding.data ?: return@setOnClickListener
            overflowMenuAdapterListener.onMenuItemClicked(data)
        }
        return binding
    }

    override fun bindData(
        binding: ItemOverflowMenuBinding,
        data: BaseViewType,
        position: Int
    ) {
        binding.data = data as OverflowMenuItemViewData
        binding.showWarning = data.showWarning
    }
}