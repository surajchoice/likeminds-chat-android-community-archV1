package com.likeminds.chatmm.conversation.view.adapter.databinder.customwidgets

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationGroupChatWidgetABinding
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.reactions.util.ReactionsPreferences
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_CUSTOM_WIDGET_A_GROUP

class ConversationGroupChatWidgetAItemViewDataBinder(
    private val userPreferences: UserPreferences,
    private val reactionsPreferences: ReactionsPreferences,
    private val adapterListener: ChatroomDetailAdapterListener
) : ViewDataBinder<ItemConversationGroupChatWidgetABinding, ConversationViewData>() {
    override val viewType: Int
        get() = ITEM_CUSTOM_WIDGET_A_GROUP

    override fun createBinder(parent: ViewGroup): ItemConversationGroupChatWidgetABinding {
        val binding = ItemConversationGroupChatWidgetABinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return binding
    }

    override fun bindData(
        binding: ItemConversationGroupChatWidgetABinding,
        data: ConversationViewData,
        position: Int
    ) {

    }
}