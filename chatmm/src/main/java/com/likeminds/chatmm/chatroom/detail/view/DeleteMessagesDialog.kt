package com.likeminds.chatmm.chatroom.detail.view

import com.likeminds.chatmm.R
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.DialogMessagesBinding
import com.likeminds.chatmm.utils.customview.BaseDialogFragment


/**
 * Dialog used to show when user is deleting messages in a chatroom
 **/
class DeleteMessagesDialog(
    private val listener: DeleteMessageListener,
    private val conversations: List<ConversationViewData>,
    private val topic: ConversationViewData?
) : BaseDialogFragment<DialogMessagesBinding>() {

    override fun getViewBinding(): DialogMessagesBinding {
        return DialogMessagesBinding.inflate(layoutInflater)
    }

    override val cancellable: Boolean
        get() = true

    override val margin: Int
        get() = 30

    companion object {
        const val TAG = "DeleteMessagesDialog"
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        binding.btnPositive.setOnClickListener {
            listener.deleteMessages(conversations, topic)
            dismiss()
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
        }
    }

    private fun initView() {
        val text = if (topic != null) {
            if (conversations.size > 1) {
                getString(R.string.topic_delete_multiple_messages)
            } else {
                getString(R.string.topic_delete_single_messages)
            }
        } else {
            if (conversations.size > 1) {
                getString(R.string.delete_multiple_message)
            } else {
                getString(R.string.delete_single_message)
            }
        }

        binding.tvMessage.text = text
    }
}

interface DeleteMessageListener {
    fun deleteMessages(
        conversations: List<ConversationViewData>,
        topic: ConversationViewData?
    )
}