package com.likeminds.chatmm.search.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.databinding.ItemSearchChatroomBinding
import com.likeminds.chatmm.search.model.SearchChatroomHeaderViewData
import com.likeminds.chatmm.search.view.adapter.SearchAdapterListener
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_SEARCH_CHATROOM

class SearchChatroomHeaderViewDataBinder(
    private val listener: SearchAdapterListener
) : ViewDataBinder<ItemSearchChatroomBinding, SearchChatroomHeaderViewData>() {

    override val viewType: Int
        get() = ITEM_SEARCH_CHATROOM

    override fun createBinder(parent: ViewGroup): ItemSearchChatroomBinding {
        val binding =
            ItemSearchChatroomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        setListeners(binding)
        return binding
    }

    override fun bindData(
        binding: ItemSearchChatroomBinding,
        data: SearchChatroomHeaderViewData,
        position: Int
    ) {
        binding.apply {
            searchChatViewData = data
            hideBottomLine = data.isLast == true

            val chatroomViewData = data.chatroom

            //set chatroom image
            ViewUtils.setChatroomImage(
                chatroomViewData.id,
                chatroomViewData.header,
                chatroomViewData.chatroomImageUrl,
                ivChatRoom
            )

            //set chatroom drawable type
            val chatTypeDrawable =
                ChatroomUtil.getTypeDrawable(root.context, chatroomViewData.type)
            if (chatTypeDrawable == null) {
                ivChatroomType.hide()
            } else {
                ivChatroomType.show()
                ivChatroomType.setImageDrawable(chatTypeDrawable)
            }
        }
    }

    private fun setListeners(binding: ItemSearchChatroomBinding) {
        binding.root.setOnClickListener {
            val searchChatroomHeaderViewData =
                binding.searchChatViewData ?: return@setOnClickListener
            listener.onChatroomClicked(searchChatroomHeaderViewData)
        }
    }
}