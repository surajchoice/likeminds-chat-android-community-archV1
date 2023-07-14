package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationPollBinding
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.polls.view.PollViewListener
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.setVisible
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_POLL

internal class ConversationPollItemViewDataBinder constructor(
    private val userPreferences: UserPreferences,
//    private val messageReactionsPreferences: MessageReactionsPreferences,
    private val chatroomDetailAdapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationPollBinding, ConversationViewData>(),
    PollViewListener {

    override val viewType: Int
        get() = ITEM_CONVERSATION_POLL

    override fun createBinder(parent: ViewGroup): ItemConversationPollBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemConversationPollBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemConversationPollBinding,
        data: ConversationViewData,
        position: Int,
    ) {
        binding.apply {
            buttonColor = LMBranding.getButtonsColor()
            viewReply.buttonColor = LMBranding.getButtonsColor()

            conversation = data
            ChatroomConversationItemViewDataBinderUtil.initConversationBubbleView(
                clConversationRoot,
                clConversationBubble,
                memberImage,
                tvConversationMemberName,
                tvCustomTitle,
                tvCustomTitleDot,
                data.memberViewData,
                userPreferences.getMemberId(),
                chatroomDetailAdapterListener,
                position,
                conversationViewData = data,
                imageViewStatus = conversationStatus,
                imageViewFailed = conversationFailed
            )

            if (data.deletedBy != null) {
                updatePollViewVisibility(this, false)
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    userPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                updatePollViewVisibility(this, true)

                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = chatroomDetailAdapterListener,
                    tvDeleteMessage = tvDeleteMessage
                )

                initializePollViews(position, binding, data)
            }

            ChatroomConversationItemViewDataBinderUtil.initTimeAndStatus(
                tvTime,
                userPreferences.getMemberId(),
                data.createdAt ?: ""
            )

            ChatroomConversationItemViewDataBinderUtil.initReplyView(
                viewReply,
                userPreferences.getMemberId(),
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
                userPreferences.getMemberId(),
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

            isSelected = ChatroomConversationItemViewDataBinderUtil.initConversationSelection(
                root,
                viewList,
                data,
                position,
                chatroomDetailAdapterListener
            )

//        val messageReactionsGridViewData = ChatroomUtil.getMessageReactionsGrid(data)
//
//        ChatroomConversationItemViewDataBinderUtil.initMessageReactionGridView(
//            messageReactionsGridViewData,
//            clConversationRoot,
//            clConversationBubble,
//            messageReactionsGridLayout,
//            userPreferences.getMemberId(),
//            chatroomDetailAdapterListener,
//            data
//        )

//        val isReactionHintShown =
//            ChatroomConversationItemViewDataBinderUtil.isReactionHintViewShown(
//                data.isLastItem,
//                messageReactionsPreferences.getHasUserReactedOnce(),
//                messageReactionsPreferences.getNoOfTimesHintShown(),
//                messageReactionsPreferences.getTotalNoOfHintsAllowed(),
//                tvDoubleTap,
//                data.memberViewData(),
//                userPreferences.getMemberId(),
//                clConversationRoot,
//                clConversationBubble
//            )
//        if (isReactionHintShown) {
//            chatroomDetailAdapterListener.messageReactionHintShown()
//        }
        }
    }

    override fun onSubmitPoll(conversationId: String) {
        val binding = getBinding(conversationId) ?: return
        submitPoll(binding)
    }

    override fun updatePollButton(conversationId: String?, enable: Boolean) {
        val binding = getBinding(conversationId) ?: return
        if (enable) {
            ChatroomConversationItemViewDataBinderUtil.enablePollButton(binding.btnSubmitVote)
        } else {
            ChatroomConversationItemViewDataBinderUtil.disablePollButton(binding.btnSubmitVote)
        }
    }

    private fun getBinding(conversationId: String?): ItemConversationPollBinding? {
        return try {
            (chatroomDetailAdapterListener.getBinding(conversationId))?.binding as? ItemConversationPollBinding
        } catch (e: Exception) {
            null
        }
    }

    private fun initializePollViews(
        position: Int,
        binding: ItemConversationPollBinding,
        data: ConversationViewData,
    ) {
        binding.apply {
            pollView.init(
                position,
                data,
                chatroomDetailAdapterListener,
                this@ConversationPollItemViewDataBinder,
                userPreferences
            )

            val pollInfoData = data.pollInfoData
            if (pollInfoData != null) {
                updatePollButtonVisibility(this)
                initPollAddButtonView(this, data.memberViewData)
                ChatroomConversationItemViewDataBinderUtil.initPollSelectText(
                    tvPollSelectText,
                    pollInfoData
                )
                ChatroomConversationItemViewDataBinderUtil.updatePollButton(
                    btnSubmitVote,
                    pollInfoData
                )
                setAddOptionClick(this, data)
                setPollSubmitClick(this)
                setMembersVotedClick(this, data)
                ChatroomConversationItemViewDataBinderUtil.initPollEndDateTimeView(
                    tvDateTimeLeft,
                    pollInfoData.expiryTime,
                    chatroomDetailAdapterListener
                )
            }

            if (!pollView.isPollSelected()) {
                chatroomDetailAdapterListener.dismissToastMessage()
            }
        }
    }

    private fun setMembersVotedClick(
        binding: ItemConversationPollBinding,
        conversation: ConversationViewData,
    ) {
        binding.tvAnswerText.setOnClickListener {
            chatroomDetailAdapterListener.onConversationMembersVotedCountClick(
                conversation,
                binding.pollView.hasPollEnded(),
                conversation.pollInfoData?.isAnonymous,
                conversation.memberViewData.id.equals(userPreferences.getMemberId())
            )
        }
    }

    private fun updatePollButtonVisibility(binding: ItemConversationPollBinding) {
        binding.apply {
            // Hide Button If poll is instant and already submitted or poll is deferred with single item selection
            if ((pollView.isInstantPoll() && pollView.isPollSubmitted()) ||
                pollView.hasPollEnded()
            ) {
                btnSubmitVote.hide()
            } else {
                if (pollView.pollData.multipleSelectState == null) {
                    btnSubmitVote.hide()
                } else {
                    btnSubmitVote.show()
                }
            }
            updateSubmitButtonTextValue(this)
        }
    }

    private fun updateSubmitButtonTextValue(binding: ItemConversationPollBinding) {
        binding.apply {
            val context = root.context
            if (pollView.isInstantPoll() || !pollView.isPollSubmitted()) {
                btnSubmitVote.text = context.getString(R.string.submit_vote)
                btnSubmitVote.setIconResource(R.drawable.ic_vote)
            } else {
                btnSubmitVote.text = context.getString(R.string.edit_vote)
                btnSubmitVote.setIconResource(R.drawable.ic_edit_24dp)
            }
        }
    }

    private fun initPollAddButtonView(
        binding: ItemConversationPollBinding, memberViewData: MemberViewData,
    ) {
        binding.apply {
            val isAddOptionAllowedForInstantPoll =
                pollView.isInstantPoll() && !pollView.isPollSubmitted()
            val isAddOptionAllowedForDeferredPoll = !pollView.isInstantPoll()
            if (pollView.pollData.allowAddOption == true && !pollView.hasPollEnded()
                && (isAddOptionAllowedForInstantPoll || isAddOptionAllowedForDeferredPoll)
            ) {
                btnAddOption.show()
            } else {
                btnAddOption.hide()
            }

            if (memberViewData.id == userPreferences.getMemberId()) {
                btnAddOption.strokeColor = ColorStateList.valueOf(LMBranding.getButtonsColor())
            } else {
                btnAddOption.setStrokeColorResource(R.color.cloudy_blue)
            }
        }
    }

    private fun setAddOptionClick(
        binding: ItemConversationPollBinding,
        data: ConversationViewData,
    ) {
        binding.btnAddOption.setOnClickListener {
            chatroomDetailAdapterListener.addConversationPollOptionClicked(data.id)
        }
    }

    private fun setPollSubmitClick(binding: ItemConversationPollBinding) {
        binding.apply {
            btnSubmitVote.setOnClickListener {
                if (btnSubmitVote.text.toString() == root.context.getString(R.string.edit_vote)) {
                    //enable editing poll
                    updatePollSubmittedValue(binding, false)
                    pollView.reload()
                    updatePollButtonVisibility(binding)
                } else {
                    // enable submitting poll
                    when (btnSubmitVote.tag) {
                        ChatroomDetailFragment.POLL_CLICK_DISABLED -> {
                            return@setOnClickListener
                        }

                        ChatroomDetailFragment.POLL_CLICK_ENABLED -> {
                            submitPoll(binding)
                            updatePollButtonVisibility(binding)
                        }
                    }
                }
            }
        }
    }

    private fun updatePollViewVisibility(binding: ItemConversationPollBinding, show: Boolean) {
        binding.apply {
            tvPollType.setVisible(show)
            viewPoint1.setVisible(show)
            tvAnonymousType.setVisible(show)
            ivChatType.setVisible(show)
            tvDateTimeLeft.setVisible(show)
            tvPollSelectText.setVisible(show)
            pollView.setVisible(show)
            btnAddOption.setVisible(show)
            tvAnswerText.setVisible(show)
            btnSubmitVote.setVisible(show)
            tvPollType.setVisible(show)
        }
    }

    private fun submitPoll(binding: ItemConversationPollBinding) {
        binding.apply {
            val conversation = conversation ?: return
            val pollItems = pollView.pollItems
            chatroomDetailAdapterListener.onConversationPollSubmitClicked(conversation, pollItems)
            chatroomDetailAdapterListener.dismissToastMessage()
            updatePollSubmittedValue(this, true)
            initPollAddButtonView(this, conversation.memberViewData)
        }
    }

    private fun updatePollSubmittedValue(binding: ItemConversationPollBinding, value: Boolean) {
        binding.pollView.updatePollSubmittedValue(value)
    }

}