package com.likeminds.chatmm.polls.adapter.databinders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.likeminds.chatmm.chatroom.create.view.adapter.CreatePollItemAdapterListener
import com.likeminds.chatmm.databinding.ItemCreatePollBinding
import com.likeminds.chatmm.polls.model.CreatePollViewData
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_CREATE_POLL

class ItemCreatePollViewDataBinder constructor(
    private val createPollItemAdapterListener: CreatePollItemAdapterListener?,
) : ViewDataBinder<ItemCreatePollBinding, CreatePollViewData>() {

    override val viewType: Int
        get() = ITEM_CREATE_POLL

    override fun createBinder(parent: ViewGroup): ItemCreatePollBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemCreatePollBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemCreatePollBinding,
        data: CreatePollViewData,
        position: Int
    ) {
        binding.apply {
            createPollViewData = data
            etPoll.setText("")
            val subText = data.subText

            val text = data.text
            if (!text.isNullOrBlank()) {
                etPoll.setText(text)
            }

            if (subText.isNullOrBlank()) {
                tvPollSubText.visibility = View.GONE
            } else {
                tvPollSubText.visibility = View.VISIBLE
                tvPollSubText.text = subText
            }
            createPollItemAdapterListener?.addPollItemBinding(position, binding)

            ivCross.setOnClickListener {
                val viewData = createPollViewData ?: return@setOnClickListener
                etPoll.clearFocus()
                ViewUtils.hideKeyboard(etPoll)
                createPollItemAdapterListener?.pollCrossed(viewData)
            }
        }
    }
}