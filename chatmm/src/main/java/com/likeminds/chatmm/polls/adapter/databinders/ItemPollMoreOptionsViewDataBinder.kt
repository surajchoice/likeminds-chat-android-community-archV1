package com.likeminds.chatmm.polls.adapter.databinders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemPollMoreOptionsBinding
import com.likeminds.chatmm.polls.model.PollMoreOptionsViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_POLL_MORE_OPTIONS

class ItemPollMoreOptionsViewDataBinder :
    ViewDataBinder<ItemPollMoreOptionsBinding, PollMoreOptionsViewData>() {

    override val viewType: Int
        get() = ITEM_POLL_MORE_OPTIONS

    override fun createBinder(parent: ViewGroup): ItemPollMoreOptionsBinding {
        return ItemPollMoreOptionsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    }

    override fun bindData(
        binding: ItemPollMoreOptionsBinding,
        data: PollMoreOptionsViewData,
        position: Int,
    ) {
        val context = binding.root.context
        val optionCount = data.optionsCount
        binding.tvPoll.text = context.resources.getQuantityString(
            R.plurals.more_option,
            optionCount,
            optionCount
        )
    }
}