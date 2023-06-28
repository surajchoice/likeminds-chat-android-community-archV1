package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.databinding.ItemConversationAutoFollowedTaggedActionBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM

internal class ConversationAutoFollowedTaggedActionViewDataBinder constructor(
    private val chatroomDetailAdapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationAutoFollowedTaggedActionBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_AUTO_FOLLOWED_TAGGED_CHAT_ROOM

    override fun createBinder(parent: ViewGroup): ItemConversationAutoFollowedTaggedActionBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            ItemConversationAutoFollowedTaggedActionBinding.inflate(inflater, parent, false)
        setButtonClick(binding)
        return binding
    }

    override fun bindData(
        binding: ItemConversationAutoFollowedTaggedActionBinding,
        data: BaseViewType,
        position: Int,
    ) {

    }

    private fun setButtonClick(binding: ItemConversationAutoFollowedTaggedActionBinding) {
        binding.btnKeepFollowing.setOnClickListener {
            chatroomDetailAdapterListener.keepFollowingChatRoomClicked()
        }

        binding.btnUnfollow.setOnClickListener {
            chatroomDetailAdapterListener.unFollowChatRoomClicked()
        }
    }

    override fun drawPrimaryColor(
        binding: ItemConversationAutoFollowedTaggedActionBinding,
        color: Int,
    ) {
        super.drawPrimaryColor(binding, color)
        binding.buttonKeepFollowing.backgroundTintList = ColorStateList.valueOf(color)
        binding.buttonUnfollow.setTextColor(color)
        binding.buttonUnfollow.strokeColor = ColorStateList.valueOf(color)
    }

    override fun drawAdvancedColor(
        binding: ItemConversationAutoFollowedTaggedActionBinding,
        headerColor: Int,
        buttonsIconsColor: Int,
        textLinksColor: Int,
    ) {
        super.drawAdvancedColor(binding, headerColor, buttonsIconsColor, textLinksColor)
        binding.buttonKeepFollowing.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)
        binding.buttonUnfollow.setTextColor(buttonsIconsColor)
        binding.buttonUnfollow.strokeColor = ColorStateList.valueOf(buttonsIconsColor)
    }

}