package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.databinding.ItemHomeFeedBinding
import com.likeminds.chatmm.homefeed.model.HomeFeedViewData
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_HOME_FEED
import javax.inject.Inject

class HomeFeedViewDataBinder @Inject constructor(
    private val homeAdapterListener: HomeFeedAdapter.HomeFeedAdapterListener?
) : ViewDataBinder<ItemHomeFeedBinding, HomeFeedViewData>() {

    override val viewType: Int
        get() = ITEM_HOME_FEED

    override fun createBinder(parent: ViewGroup): ItemHomeFeedBinding {
        val binding = ItemHomeFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setRootClick(binding)
        return binding
    }

    private fun setRootClick(binding: ItemHomeFeedBinding) {
        binding.root.setOnClickListener {
            homeAdapterListener?.homeFeedClicked()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(
        binding: ItemHomeFeedBinding,
        data: HomeFeedViewData,
        position: Int
    ) {
        binding.apply {
            buttonColor = LMBranding.getButtonsColor()
            this.data = data

            val context = this.root.context
            if (data.newChatRooms > 0) {
                tvUnreadChatroom.isVisible = true
                if (data.newChatRooms > 99) {
                    tvUnreadChatroom.text = context.getString(R.string.ninety_nine_plus_new)
                } else {
                    tvUnreadChatroom.text =
                        context.getString(R.string.new_chatroom_count, data.newChatRooms)
                }
            } else if (data.totalChatRooms > 0) {
                tvUnreadChatroom.isVisible = true
                if (data.totalChatRooms > 99) {
                    tvUnreadChatroom.text = context.getString(R.string.ninety_nine_plus_chatrooms)
                } else {
                    tvUnreadChatroom.text = context.resources.getQuantityString(
                        R.plurals.chatrooms_count,
                        data.totalChatRooms,
                        data.totalChatRooms
                    )
                }
            } else {
                tvUnreadChatroom.isVisible = false
            }
        }
    }
}