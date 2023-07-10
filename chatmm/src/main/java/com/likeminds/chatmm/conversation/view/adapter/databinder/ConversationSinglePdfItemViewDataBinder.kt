package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationSinglePdfBinding
import com.likeminds.chatmm.utils.AndroidUtils
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_SINGLE_PDF

internal class ConversationSinglePdfItemViewDataBinder constructor(
    private val sdkPreferences: SDKPreferences,
//    private val messageReactionsPreferences: MessageReactionsPreferences,
    private val adapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationSinglePdfBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_SINGLE_PDF

    override fun createBinder(parent: ViewGroup): ItemConversationSinglePdfBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemConversationSinglePdfBinding.inflate(inflater, parent, false)
        initSinglePdfViewClick(binding)
        return binding
    }

    override fun bindData(
        binding: ItemConversationSinglePdfBinding,
        data: BaseViewType,
        position: Int,
    ) {
        binding.apply {
            buttonColor = LMBranding.getButtonsColor()
            viewReply.buttonColor = LMBranding.getButtonsColor()
            conversation = data as ConversationViewData
            itemPosition = position
            ChatroomConversationItemViewDataBinderUtil.initConversationBubbleView(
                clConversationRoot,
                clConversationBubble,
                memberImage,
                tvConversationMemberName,
                tvCustomTitle,
                tvCustomTitleDot,
                data.memberViewData,
                sdkPreferences.getMemberId(),
                adapterListener,
                position,
                conversationViewData = data,
                imageViewStatus = ivConversationStatus,
                imageViewFailed = ivConversationFailed
            )

            if (data.deletedBy != null) {
                clImage.hide()
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    sdkPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                clImage.show()
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = adapterListener,
                    tvDeleteMessage = tvDeleteMessage
                )
                initSinglePdfView(this, data)
            }

//            ChatroomConversationItemViewDataBinderUtil.initReactionButton(
//                ivAddReaction,
//                data,
//                sdkPreferences.getMemberId()
//            )

            ChatroomConversationItemViewDataBinderUtil.initDocument(binding, data)

            ChatroomConversationItemViewDataBinderUtil.initProgress(binding.tvProgress, data)

            ChatroomConversationItemViewDataBinderUtil.initTimeAndStatus(
                tvTime,
                sdkPreferences.getMemberId(),
                data.createdAt,
                data.answer.isEmpty() && (data.deletedBy == null),
                imageViewStatus = ivConversationStatus,
                conversation = data,
                data.attachments?.firstOrNull()?.meta != null
            )

            ChatroomConversationItemViewDataBinderUtil.initReplyView(
                viewReply,
                sdkPreferences.getMemberId(),
                data.replyConversation,
                data.replyChatroomId,
                adapterListener,
                itemPosition = position,
                conversation = data
            )

            ChatroomConversationItemViewDataBinderUtil.initSelectionAnimation(
                viewSelectionAnimation,
                position,
                adapterListener
            )

            ChatroomConversationItemViewDataBinderUtil.initReportView(
                ivReport,
                sdkPreferences.getMemberId(),
                adapterListener,
                conversationViewData = data
            )

            val viewList = listOf(
                root,
                memberImage,
                tvConversation,
                ivSingleImage,
                tvPdfName,
                viewReply.root,
                ivReport
            )
            isSelected = ChatroomConversationItemViewDataBinderUtil.initConversationSelection(
                root,
                viewList,
                data,
                position,
                adapterListener
            )

//            val messageReactionsGridViewData = ChatroomUtil.getMessageReactionsGrid(data)
//
//            ChatroomConversationItemViewDataBinderUtil.initMessageReactionGridView(
//                messageReactionsGridViewData,
//                clConversationRoot,
//                clConversationBubble,
//                messageReactionsGridLayout,
//                sdkPreferences.getMemberId(),
//                adapterListener,
//                data
//            )
//            val isReactionHintShown =
//                ChatroomConversationItemViewDataBinderUtil.isReactionHintViewShown(
//                    data.isLastItem,
//                    messageReactionsPreferences.getHasUserReactedOnce(),
//                    messageReactionsPreferences.getNoOfTimesHintShown(),
//                    messageReactionsPreferences.getTotalNoOfHintsAllowed(),
//                    tvDoubleTap,
//                    data.memberViewData,
//                    sdkPreferences.getMemberId(),
//                    clConversationRoot,
//                    clConversationBubble
//                )
//            if (isReactionHintShown) {
//                adapterListener.messageReactionHintShown()
//            }
        }
    }

    private fun initSinglePdfView(
        binding: ItemConversationSinglePdfBinding,
        conversation: ConversationViewData,
    ) {
        binding.apply {
            val mediaUploadData = ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                viewMediaUploadingActions,
                conversation = conversation,
                listener = adapterListener
            )
            if (mediaUploadData.first != null) {
                adapterListener.observeMediaUpload(mediaUploadData.first!!, conversation)
            }

            val attachment = conversation.attachments?.get(0) ?: return
            tvPdfName.text = attachment.name ?: "Document"
            if (!attachment.thumbnail.isNullOrEmpty()) {
                tvPdfName.setBackgroundResource(R.drawable.background_item_document_name_with_preview)
            } else {
                tvPdfName.setBackgroundResource(R.drawable.background_item_document_name_without_preview)
            }
            if (attachment.thumbnail.isNullOrEmpty()) {
                ivSingleImage.hide()
                tvPdfNameTopLine.hide()
            } else {
                ivSingleImage.show()
                tvPdfNameTopLine.show()
                ImageBindingUtil.loadImage(
                    binding.ivSingleImage,
                    attachment.thumbnail,
                    placeholder = R.drawable.image_placeholder,
                    cornerRadius = 10
                )
                ChatroomConversationItemViewDataBinderUtil.initImageAspectRatio(
                    clImage,
                    ivSingleImage,
                    attachment
                )
            }
        }
    }

    private fun initSinglePdfViewClick(binding: ItemConversationSinglePdfBinding) {
        binding.apply {
            ivSingleImage.setOnClickListener {
                adapterListener.onScreenChanged()
                onPdfClicked(this)
            }

            tvPdfName.setOnClickListener {
                adapterListener.onScreenChanged()
                onPdfClicked(this)
            }

            ivAddReaction.setOnClickListener {
                val conversation = conversation ?: return@setOnClickListener
                val itemPosition = itemPosition ?: return@setOnClickListener
                adapterListener.onLongPressConversation(
                    conversation,
                    itemPosition,
                    LMAnalytics.Source.MESSAGE_REACTIONS_FROM_REACTION_BUTTON
                )
            }
        }
    }

    private fun onPdfClicked(binding: ItemConversationSinglePdfBinding) {
        binding.apply {
            val actionsVisible = viewMediaUploadingActions.actionsVisible
            if (actionsVisible == true) return

            val conversation = conversation ?: return
            val itemPosition = itemPosition
            if (itemPosition != null && adapterListener.isSelectionEnabled()) {
                adapterListener.onLongPressConversation(
                    conversation,
                    itemPosition,
                    LMAnalytics.Source.MESSAGE_REACTIONS_FROM_LONG_PRESS
                )
            } else {
                val uri = conversation.attachments?.get(0)?.uri ?: return
                AndroidUtils.startDocumentViewer(root.context, uri)
            }
        }
    }
}