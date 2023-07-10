package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapter
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationMultipleDocumentBinding
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_MULTIPLE_DOCUMENT
import com.likeminds.chatmm.utils.model.ITEM_DOCUMENT

internal class ConversationMultipleDocumentViewDataBinder constructor(
    private val sdkPreferences: SDKPreferences,
    private val chatroomDetailAdapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationMultipleDocumentBinding, BaseViewType>(),
    ChatroomItemAdapterListener {

    companion object {
        const val SHOW_MORE_COUNT = 2
    }

    private var mediaActionVisible: Boolean = false

    override val viewType: Int
        get() = ITEM_CONVERSATION_MULTIPLE_DOCUMENT

    override fun createBinder(parent: ViewGroup): ItemConversationMultipleDocumentBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemConversationMultipleDocumentBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemConversationMultipleDocumentBinding,
        data: BaseViewType,
        position: Int,
    ) {
        binding.apply {
            buttonColor = LMBranding.getButtonsColor()
            textLinkColor = LMBranding.getTextLinkColor()
            viewReply.buttonColor = LMBranding.getButtonsColor()
            conversation = data as ConversationViewData
            itemPosition = position

            ChatroomConversationItemViewDataBinderUtil.initConversationBubbleView(
                clConversationRoot,
                clConversationBubble,
                memberImage,
                tvMemberName,
                tvCustomTitle,
                tvCustomTitleDot,
                data.memberViewData,
                sdkPreferences.getMemberId(),
                chatroomDetailAdapterListener,
                position,
                conversationViewData = data,
                imageViewStatus = ivConversationStatus,
                imageViewFailed = ivConversationFailed
            )

            if (data.deletedBy != null) {
                rvDocuments.visibility = View.GONE
                tvShowMore.hide()
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    sdkPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                rvDocuments.visibility = View.VISIBLE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = chatroomDetailAdapterListener,
                    tvDeleteMessage = binding.tvDeleteMessage
                )
                initDocumentsListView(this, data, position)
            }

//            ChatroomConversationItemViewDataBinderUtil.initReactionButton(
//                ivAddReaction,
//                data,
//                sdkPreferences.getMemberId()
//            )

            ChatroomConversationItemViewDataBinderUtil.initProgress(binding.tvProgress, data)

            ChatroomConversationItemViewDataBinderUtil.initTimeAndStatus(
                tvTime,
                sdkPreferences.getMemberId(),
                data.createdAt,
                data.answer.isEmpty() && data.deletedBy == null,
                imageViewStatus = ivConversationStatus,
                conversation = data
            )

            ChatroomConversationItemViewDataBinderUtil.initReplyView(
                viewReply,
                sdkPreferences.getMemberId(),
                data.replyConversation,
                data.replyChatroomId,
                chatroomDetailAdapterListener,
                itemPosition = position,
                conversation = data
            )

            ChatroomConversationItemViewDataBinderUtil.initSelectionAnimation(
                viewSelectionAnimation,
                position,
                chatroomDetailAdapterListener
            )

            ChatroomConversationItemViewDataBinderUtil.initReportView(
                ivReport,
                sdkPreferences.getMemberId(),
                chatroomDetailAdapterListener,
                conversationViewData = data
            )

            val viewList = listOf(
                root,
                memberImage,
                tvConversation,
                viewReply.root,
                ivReport
            )
            isSelected =
                ChatroomConversationItemViewDataBinderUtil.initConversationSelection(
                    root,
                    viewList,
                    data,
                    position,
                    chatroomDetailAdapterListener
                )

//            val messageReactionsGridViewData = ChatroomUtil.getMessageReactionsGrid(data)
//
//            ChatroomConversationItemViewDataBinderUtil.initMessageReactionGridView(
//                messageReactionsGridViewData,
//                clConversationRoot,
//                clConversationBubble,
//                messageReactionsGridLayout,
//                sdkPreferences.getMemberId(),
//                chatroomDetailAdapterListener,
//                data
//            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initDocumentsListView(
        binding: ItemConversationMultipleDocumentBinding,
        conversation: ConversationViewData,
        position: Int,
    ) {
        binding.apply {
            val documentsItemAdapter = ChatroomItemAdapter(
                sdkPreferences,
                chatroomItemAdapterListener = this@ConversationMultipleDocumentViewDataBinder
            )
            rvDocuments.adapter = documentsItemAdapter

            val attachments = conversation.attachments.orEmpty().map {
                it.toBuilder()
                    .parentViewItemPosition(position)
                    .parentConversation(conversation)
                    .dynamicType(ITEM_DOCUMENT)
                    .build()
            }

            val tvConversationLP =
                tvConversation.layoutParams as ViewGroup.MarginLayoutParams
            if (conversation.isExpanded || attachments.size <= SHOW_MORE_COUNT) {
                tvShowMore.hide()
                tvConversationLP.topMargin = ViewUtils.dpToPx(4)
                documentsItemAdapter.replace(attachments)
            } else {
                tvShowMore.show()
                tvShowMore.text = "+${attachments.size - SHOW_MORE_COUNT} more"
                tvConversationLP.topMargin = ViewUtils.dpToPx(15)
                documentsItemAdapter.replace(attachments.take(SHOW_MORE_COUNT))
            }
            tvConversation.layoutParams = tvConversationLP

            val mediaUploadData = ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                viewMediaUploadingActions,
                conversation = conversation,
                listener = chatroomDetailAdapterListener
            )

            mediaActionVisible = mediaUploadData.second

            if (mediaUploadData.first != null) {
                chatroomDetailAdapterListener.observeMediaUpload(
                    mediaUploadData.first!!, conversation
                )
            }

            binding.tvShowMore.setOnClickListener {
                chatroomDetailAdapterListener.onMultipleItemsExpanded(conversation, position)
            }

            binding.ivAddReaction.setOnClickListener {
                chatroomDetailAdapterListener.onLongPressConversation(
                    conversation, position,
                    LMAnalytics.Source.MESSAGE_REACTIONS_FROM_REACTION_BUTTON
                )
            }
        }
    }

    override fun onLongPressConversation(
        conversation: ConversationViewData,
        itemPosition: Int,
    ) {
        chatroomDetailAdapterListener.onLongPressConversation(
            conversation,
            itemPosition,
            LMAnalytics.Source.MESSAGE_REACTIONS_FROM_LONG_PRESS
        )
    }

    override fun isSelectionEnabled(): Boolean {
        return chatroomDetailAdapterListener.isSelectionEnabled()
    }

    override fun isMediaActionVisible(): Boolean {
        return mediaActionVisible
    }

    override fun onScreenChanged() {
        chatroomDetailAdapterListener.onScreenChanged()
    }
}