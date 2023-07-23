package com.likeminds.chatmm.polls.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.*
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.chatroom.create.view.adapter.*
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil.hasPollEnded
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil.isDeferredPoll
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil.isInstantPoll
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil.isMultipleItemPoll
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.LayoutPollViewBinding
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.polls.model.*

class PollView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs),
    ChatroomItemAdapterListener,
    CreatePollItemAdapterListener,
    PollItemAdapterListener {

    private lateinit var adapter: ChatroomItemAdapter
    lateinit var pollData: PollInfoData
    private lateinit var userPreferences: UserPreferences
    private lateinit var listener: ChatroomDetailAdapterListener
    private lateinit var pollViewListener: PollViewListener
    private lateinit var conversationId: String

    val pollItems get() = pollData.pollViewDataList.orEmpty()

    private var binding = LayoutPollViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun init(
        position: Int,
        conversation: ConversationViewData,
        listener: ChatroomDetailAdapterListener,
        pollViewListener: PollViewListener,
        userPreferences: UserPreferences,
    ) {
        this.pollData = conversation.pollInfoData ?: return
        this.listener = listener
        this.pollViewListener = pollViewListener
        this.conversationId = conversation.id
        this.userPreferences = userPreferences

        val items = pollItems.map {
            it.toBuilder()
                .pollInfoData(pollData)
                .parentConversation(conversation)
                .parentId(conversation.id)
                .toShowResults(pollData.toShowResult)
                .parentViewItemPosition(position)
                .build()
        }
        adapter = ChatroomItemAdapter(
            userPreferences,
            chatroomItemAdapterListener = this,
            createPollItemAdapterListener = this,
            pollItemAdapterListener = this,
        )
        binding.rvPolls.adapter = adapter
        adapter.replace(items)
    }

    override fun isPollSubmitted(): Boolean {
        return pollData.isPollSubmitted ?: false
    }

    override fun isPollSelected(): Boolean {
        return pollItems.firstOrNull { it.isSelected == true } != null
    }

    override fun showVotersList(
        pollId: String?,
        parentId: String?,
        pollInfoData: PollInfoData?,
        positionOfPoll: Int,
    ) {
        if (pollInfoData?.isAnonymous == true) {
            ChatroomConversationItemViewDataBinderUtil.showAnonymousPollDialog(context)
        } else {
            if (!parentId.isNullOrEmpty()) {
                listener.showConversationPollVotersList(
                    parentId,
                    pollId,
                    hasPollEnded(),
                    pollInfoData?.toShowResult,
                    positionOfPoll
                )
            }
        }
    }

    override fun pollSelected(context: Context, pollViewData: PollViewData) {
        if (isPollSubmitted() && !(pollViewData.pollInfoData.isDeferredPoll())) {
            return
        }
        if (pollData.isMultipleItemPoll()) {
            //Multiple Options can be Selected
            val optionCount = pollData.multipleSelectNum ?: return
            when (pollData.multipleSelectState) {
                POLL_MULTIPLE_STATE_EXACTLY -> {
                    //exactly
                    ChatroomConversationItemViewDataBinderUtil.multipleItemSelectPollExactly(
                        context,
                        pollViewData,
                        optionCount,
                        pollItems,
                        listener,
                        null,
                        pollViewListener,
                        conversationId
                    ) { list ->
                        updatePollItems(list)
                    }
                }

                POLL_MULTIPLE_STATE_MAX -> {
                    //at max
                    ChatroomConversationItemViewDataBinderUtil.multipleItemSelectPollAtMax(
                        context,
                        pollViewData,
                        optionCount,
                        pollItems,
                        listener,
                        null,
                        pollViewListener,
                        conversationId
                    ) { list ->
                        updatePollItems(list)
                    }
                }

                POLL_MULTIPLE_STATE_LEAST -> {
                    //at least
                    ChatroomConversationItemViewDataBinderUtil.multipleItemSelectPollAtLeast(
                        context,
                        pollViewData,
                        optionCount,
                        pollItems,
                        listener,
                        null,
                        pollViewListener,
                        conversationId
                    ) { list ->
                        updatePollItems(list)
                    }
                }
            }
        } else {
            //Single Options can be selected
            ChatroomConversationItemViewDataBinderUtil.singlePollItemSelected(
                pollViewData,
                pollItems
            ) { list ->
                updatePollItems(list)
                pollViewListener.onSubmitPoll(conversationId)
            }
        }
    }

    fun hasPollEnded() = pollData.hasPollEnded()

    fun isInstantPoll() = pollData.isInstantPoll()

    private fun updatePollItems(items: List<PollViewData>) {
        pollData = pollData.toBuilder()
            .pollViewDataList(items)
            .build()
        adapter.replace(items)
    }

    fun updatePollSubmittedValue(value: Boolean) {
        pollData = pollData.toBuilder()
            .isPollSubmitted(value)
            .build()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reload() {
        adapter.notifyDataSetChanged()
    }

    override fun onLongPressConversation(
        conversation: ConversationViewData,
        itemPosition: Int,
    ) {
        listener.onLongPressConversation(
            conversation,
            itemPosition,
            LMAnalytics.Source.MESSAGE_REACTIONS_FROM_LONG_PRESS
        )
    }

    override fun isSelectionEnabled(): Boolean {
        return listener.isSelectionEnabled()
    }

}

interface PollViewListener {

    fun onSubmitPoll(conversationId: String)

    fun updatePollButton(conversationId: String?, enable: Boolean)
}