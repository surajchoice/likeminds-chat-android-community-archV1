package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationSingleImageBinding
import com.likeminds.chatmm.media.model.MEDIA_HORIZONTAL_LIST_SCREEN
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.ProgressHelper
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_SINGLE_IMAGE
import com.likeminds.chatmm.utils.model.ITEM_IMAGE_SWIPE

internal class ConversationSingleImageItemViewDataBinder constructor(
    private val sdkPreferences: SDKPreferences,
//    private val messageReactionsPreferences: MessageReactionsPreferences,
    private val adapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationSingleImageBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_SINGLE_IMAGE

    override fun createBinder(parent: ViewGroup): ItemConversationSingleImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemConversationSingleImageBinding.inflate(inflater, parent, false)
        initSingleImageViewClick(binding)
        return binding
    }

    override fun bindData(
        binding: ItemConversationSingleImageBinding,
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
                clImage.visibility = View.GONE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    sdkPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                clImage.visibility = View.VISIBLE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = adapterListener,
                    tvDeleteMessage = tvDeleteMessage
                )
                initSingleImageView(this, data)
            }

//        ChatroomConversationItemViewDataBinderUtil.initReactionButton(
//            ivAddReaction,
//            data,
//            sdkPreferences.getMemberId()
//        )

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
//                    data.memberViewData(),
//                    sdkPreferences.getMemberId(),
//                    clConversationRoot,
//                    clConversationBubble
//                )
//            if (isReactionHintShown) {
//                adapterListener.messageReactionHintShown()
//            }
        }
    }

    private fun initSingleImageViewClick(binding: ItemConversationSingleImageBinding) {
        binding.apply {
            ivSingleImage.setOnClickListener {
                val actionsVisible = viewMediaUploadingActions.actionsVisible
                if (actionsVisible == true) return@setOnClickListener

                val conversation = conversation ?: return@setOnClickListener
                val itemPosition = itemPosition
                if (itemPosition != null && adapterListener.isSelectionEnabled()) {
                    adapterListener.onLongPressConversation(
                        conversation,
                        itemPosition,
                        LMAnalytics.Source.MESSAGE_REACTIONS_FROM_LONG_PRESS
                    )
                } else {
                    val imageUri = conversation.attachments?.get(0)?.uri
                    val subTitle = "${conversation.date ?: ""}, ${conversation.createdAt ?: ""}"
                    adapterListener.onScreenChanged()
                    val extra = MediaExtras.Builder()
                        .conversationId(conversation.id)
                        .chatroomId(conversation.chatroomId)
                        .communityId(conversation.communityId?.toIntOrNull())
                        .mediaScreenType(MEDIA_HORIZONTAL_LIST_SCREEN)
                        .medias(
                            listOf(
                                MediaSwipeViewData.Builder()
                                    .dynamicViewType(ITEM_IMAGE_SWIPE)
                                    .uri(imageUri ?: Uri.EMPTY)
                                    .title(conversation.memberViewData.name ?: "")
                                    .subTitle(subTitle).build()
                            )
                        )
                        .build()
                    MediaActivity.startActivity(
                        root.context,
                        extra
                    )
                }
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

    private fun initSingleImageView(
        binding: ItemConversationSingleImageBinding,
        conversationViewData: ConversationViewData,
    ) {
        binding.apply {
            if (!conversationViewData.attachments.isNullOrEmpty()) {
                val attachmentViewData = conversationViewData.attachments.firstOrNull()
                if (attachmentViewData != null) {
                    ProgressHelper.hideProgress(progressBar)
                    ivSingleImage.visibility = View.VISIBLE
                    ImageBindingUtil.loadImage(
                        ivSingleImage,
                        attachmentViewData.uri,
                        placeholder = R.drawable.image_placeholder,
                        cornerRadius = 10
                    )

                    ChatroomConversationItemViewDataBinderUtil.initImageAspectRatio(
                        clImage,
                        ivSingleImage,
                        attachmentViewData
                    )

                    val uploadData =
                        ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                            viewMediaUploadingActions,
                            conversation = conversationViewData,
                            listener = adapterListener
                        )

                    if (uploadData.first != null) {
                        adapterListener.observeMediaUpload(
                            uploadData.first!!, conversationViewData
                        )
                    }
                } else {
                    ivSingleImage.visibility = View.GONE
                    ProgressHelper.showProgress(progressBar, false)
                }
            } else {
                ivSingleImage.visibility = View.GONE
                ProgressHelper.hideProgress(progressBar)
            }
        }
    }

}