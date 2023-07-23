package com.likeminds.chatmm.chatroom.create.view.adapter

import com.likeminds.chatmm.chatroom.create.view.adapter.databinder.ItemAudioViewDataBinder
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ChatroomItemAdapter constructor(
    val chatroomItemAdapterListener: ChatroomItemAdapterListener? = null,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(20)

        val audioItemViewDataBinder = ItemAudioViewDataBinder(chatroomItemAdapterListener)
        viewDataBinders.add(audioItemViewDataBinder)

        return viewDataBinders
    }
}

interface ChatroomItemAdapterListener {
    fun onLongPressChatRoom(chatRoom: ChatroomViewData, itemPosition: Int) {}
    fun onLongPressConversation(conversation: ConversationViewData, itemPosition: Int) {}

    fun isSelectionEnabled(): Boolean {
        return false
    }

    fun isMediaActionVisible(): Boolean {
        return false
    }

    fun isMediaUploadFailed(): Boolean {
        return false
    }

    fun showMemberProfile(memberViewData: MemberViewData) {}

    fun showMembers(screenType: Int) {}

    fun onAudioConversationActionClicked(
        data: AttachmentViewData,
        position: String,
        childPosition: Int,
        progress: Int,
    ) {
    }

    fun onConversationSeekBarChanged(
        progress: Int,
        attachmentViewData: AttachmentViewData,
        parentConversationId: String,
        childPosition: Int,
    ) {
    }

    fun onSeekBarFocussed(value: Boolean) {}

    fun onScreenChanged() {}
}