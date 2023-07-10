package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationAudioBinding
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_AUDIO
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_AUDIO
import javax.inject.Inject

class ConversationAudioItemViewBinder @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val chatroomDetailAdapterListener: ChatroomDetailAdapterListener
) : ViewDataBinder<ItemConversationAudioBinding, BaseViewType>(),
    ChatroomItemAdapterListener {

    companion object {
        const val SHOW_MORE_COUNT = 2
    }

    override val viewType: Int
        get() = ITEM_CONVERSATION_AUDIO

    override fun createBinder(parent: ViewGroup): ItemConversationAudioBinding {
        return ItemConversationAudioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemConversationAudioBinding,
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
                audioView.visibility = View.GONE
                tvShowMore.hide()
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    sdkPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                audioView.visibility = View.VISIBLE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = chatroomDetailAdapterListener,
                    tvDeleteMessage = binding.tvDeleteMessage
                )
                initAudioListView(binding, data, position)
            }

//            ChatroomConversationItemViewDataBinderUtil.initReactionButton(
//                ivAddReaction,
//                data,
//                sdkPreferences.getMemberId()
//            )

            ChatroomConversationItemViewDataBinderUtil.initProgress(tvProgress, data)

            ChatroomConversationItemViewDataBinderUtil.initTimeAndStatus(
                tvTime,
                sdkPreferences.getMemberId(),
                data.createdAt,
                data.answer.isEmpty() && data.deletedBy == null,
                imageViewStatus = ivConversationStatus,
                conversation = data,
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
                viewSelectionAnimation, position, chatroomDetailAdapterListener
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
                audioView,
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
    private fun initAudioListView(
        binding: ItemConversationAudioBinding,
        data: ConversationViewData,
        position: Int,
    ) {
        binding.apply {
            val mediaUploadData = ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                viewMediaUploadingActions,
                conversation = data,
                listener = chatroomDetailAdapterListener
            )

            val mediaActionVisible = if (mediaUploadData.third == "sending") {
                true
            } else {
                mediaUploadData.second
            }
            val mediaUploadFailed = data.isFailed()

            if (mediaUploadData.first != null) {
                chatroomDetailAdapterListener.observeMediaUpload(
                    mediaUploadData.first!!, data
                )
            }

            val attachments = data.attachments.orEmpty().map {
                it.toBuilder()
                    .parentViewItemPosition(position)
                    .parentConversation(data)
                    .dynamicType(ITEM_AUDIO)
                    .build()
            }.sortedBy { it.index }

            audioView.initialize(
                attachments.take(2),
                chatroomDetailAdapterListener,
                mediaActionVisible,
                sdkPreferences,
                mediaUploadFailed
            )

            val tvConversationLP =
                tvConversation.layoutParams as ViewGroup.MarginLayoutParams
            if (data.isExpanded || attachments.size <= SHOW_MORE_COUNT) {
                tvShowMore.hide()
                tvConversationLP.topMargin = ViewUtils.dpToPx(4)
                audioView.replaceList(attachments)
            } else {
                tvShowMore.show()
                tvShowMore.text = "+${attachments.size - SHOW_MORE_COUNT} more"
                tvConversationLP.topMargin = ViewUtils.dpToPx(15)
                audioView.replaceList(attachments.take(SHOW_MORE_COUNT))
            }
            tvConversation.layoutParams = tvConversationLP

            tvShowMore.setOnClickListener {
                chatroomDetailAdapterListener.onMultipleItemsExpanded(data, position)
            }

            ivAddReaction.setOnClickListener {
                chatroomDetailAdapterListener.onLongPressConversation(
                    data,
                    position,
                    LMAnalytics.Source.MESSAGE_REACTIONS_FROM_REACTION_BUTTON
                )
            }
        }
    }
}