package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapter
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationMultipleMediaBinding
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_MULTIPLE_MEDIA
import javax.inject.Inject

internal class ConversationMultipleMediaItemViewDataBinder @Inject constructor(
    private val sdkPreferences: SDKPreferences,
    private val adapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationMultipleMediaBinding, BaseViewType>(),
    ChatroomItemAdapterListener {

    private var mediaActionVisible: Boolean = false

    override val viewType: Int
        get() = ITEM_CONVERSATION_MULTIPLE_MEDIA

    override fun drawPrimaryColor(binding: ItemConversationMultipleMediaBinding, color: Int) {
        super.drawPrimaryColor(binding, color)
        binding.viewMediaUploadingActions.progressBarLoading.progressTintList =
            ColorStateList.valueOf(color)

        binding.viewReply.viewVerticalLine.backgroundTintList = ColorStateList.valueOf(color)
        binding.viewReply.tvConversationMemberName.setTextColor(color)
        binding.viewSelection.backgroundTintList = ColorStateList.valueOf(color)
        binding.viewSelectionAnimation.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun drawAdvancedColor(
        binding: ItemConversationMultipleMediaBinding,
        headerColor: Int,
        buttonsIconsColor: Int,
        textLinksColor: Int,
    ) {
        super.drawAdvancedColor(binding, headerColor, buttonsIconsColor, textLinksColor)
        binding.viewMediaUploadingActions.progressBarLoading.progressTintList =
            ColorStateList.valueOf(buttonsIconsColor)

        binding.viewReply.viewVerticalLine.backgroundTintList =
            ColorStateList.valueOf(buttonsIconsColor)
        binding.viewReply.tvConversationMemberName.setTextColor(buttonsIconsColor)
        binding.viewSelection.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)
        binding.viewSelectionAnimation.backgroundTintList =
            ColorStateList.valueOf(buttonsIconsColor)
    }

    override fun createBinder(parent: ViewGroup): ItemConversationMultipleMediaBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemConversationMultipleMediaBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemConversationMultipleMediaBinding,
        data: BaseViewType,
        position: Int,
    ) {
        binding.apply {
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
                adapterListener,
                position,
                conversationViewData = data,
                imageViewStatus = ivConversationStatus,
                imageViewFailed = ivConversationFailed
            )

            if (data.deletedBy != null) {
                rvDocuments.visibility = View.GONE
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
                    adapterListener = adapterListener,
                    tvDeleteMessage = tvDeleteMessage
                )
                initDocumentsListView(binding, data, position)
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
                viewReply.root,
                ivReport
            )
            isSelected =
                ChatroomConversationItemViewDataBinderUtil.initConversationSelection(
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
        }
    }

    private fun initDocumentsListView(
        binding: ItemConversationMultipleMediaBinding,
        conversationViewData: ConversationViewData,
        position: Int,
    ) {
        binding.apply {
            val gridLayoutManager = GridLayoutManager(root.context, 2)
            rvDocuments.layoutManager = gridLayoutManager
            val documentsItemAdapter = ChatroomItemAdapter(
                sdkPreferences,
                chatroomItemAdapterListener = this@ConversationMultipleMediaItemViewDataBinder
            )
            rvDocuments.adapter = documentsItemAdapter
            documentsItemAdapter.replace(
                ChatroomConversationItemViewDataBinderUtil.getAttachmentViewDataList(
                    conversationViewData.attachments,
                    parentViewItemPosition = position,
                    parentConversation = conversationViewData
                )
            )

            val mediaUploadData = ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                viewMediaUploadingActions,
                conversation = conversationViewData,
                listener = adapterListener
            )

            mediaActionVisible = mediaUploadData.second

            if (mediaUploadData.first != null) {
                adapterListener.observeMediaUpload(
                    mediaUploadData.first!!, conversationViewData
                )
            }

            ivAddReaction.setOnClickListener {
                adapterListener.onLongPressConversation(
                    conversationViewData,
                    position,
                    LMAnalytics.Source.SOURCE_MESSAGE_REACTIONS_FROM_REACTION_BUTTON
                )
            }
        }
    }

    override fun onLongPressConversation(
        conversation: ConversationViewData,
        itemPosition: Int,
    ) {
        adapterListener.onLongPressConversation(
            conversation,
            itemPosition,
            LMAnalytics.Source.SOURCE_MESSAGE_REACTIONS_FROM_LONG_PRESS
        )
    }

    override fun isSelectionEnabled(): Boolean {
        return adapterListener.isSelectionEnabled()
    }

    override fun isMediaActionVisible(): Boolean {
        return mediaActionVisible
    }

    override fun onScreenChanged() {
        adapterListener.onScreenChanged()
    }
}