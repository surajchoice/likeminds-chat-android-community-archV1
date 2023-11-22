package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import androidx.databinding.DataBindingUtil
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.detail.model.TYPE_DIRECT_MESSAGE
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.*
import com.likeminds.chatmm.databinding.ItemConversationActionBinding
import com.likeminds.chatmm.member.model.MemberBlockState
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.membertagging.MemberTaggingDecoder
import com.likeminds.chatmm.utils.membertagging.util.MemberTaggingClickableSpan
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_ACTION

class ConversationActionItemViewDataBinder constructor(
    private val userPreferences: UserPreferences,
    private val chatroomDetailAdapterListener: ChatroomDetailAdapterListener?,
) : ViewDataBinder<ItemConversationActionBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_ACTION

    override fun createBinder(parent: ViewGroup): ItemConversationActionBinding {
        val inflater = LayoutInflater.from(parent.context)
        return DataBindingUtil.inflate(
            inflater, R.layout.item_conversation_action, parent, false
        )
    }

    override fun bindData(
        binding: ItemConversationActionBinding,
        data: BaseViewType,
        position: Int,
    ) {
        binding.conversation = data as ConversationViewData
        initActionTextView(
            binding.tvAction,
            data,
            position
        )
    }

    private fun initActionTextView(
        tvAction: TextView,
        conversation: ConversationViewData,
        position: Int
    ) {
        tvAction.apply {
            MemberTaggingDecoder.decode(
                tvAction,
                conversation.answer,
                true,
                ContextCompat.getColor(
                    tvAction.context,
                    R.color.white
                )
            ) { tag ->
                // todo: onMemberClick()
            }

            val editable = tvAction.editableText
            val spans = tvAction.editableText.getSpans(
                0,
                tvAction.editableText.length,
                MemberTaggingClickableSpan::class.java
            )
            val loggedInMemberUUID = userPreferences.getUUID()
            val loggedInMemberId = userPreferences.getMemberId()

            spans.reversed().forEach { span ->
                if (conversation.state != STATE_DM_MEMBER_REMOVED_OR_LEFT ||
                    conversation.state != STATE_DM_CM_BECOMES_MEMBER_DISABLE ||
                    conversation.state != STATE_DM_MEMBER_BECOMES_CM ||
                    conversation.state != STATE_DM_CM_BECOMES_MEMBER_ENABLE ||
                    conversation.state != STATE_DM_MEMBER_BECOMES_CM_ENABLE
                ) {
                    if ((span.getMemberUUID() == loggedInMemberUUID) || (span.getMemberUUID() == loggedInMemberId)) {
                        val startIndex = editable.getSpanStart(span)
                        val endIndex = editable.getSpanEnd(span)
                        if (chatroomDetailAdapterListener?.getChatRoomType() == TYPE_DIRECT_MESSAGE && conversation.state == STATE_HEADER) {
                            editable.replace(startIndex, endIndex, "")
                        } else {
                            editable.replace(startIndex, endIndex, "You")
                        }
                    }
                }
            }

            val tapToUndoString = tvAction.context.getString(R.string.tap_to_undo)

            if (conversation.showTapToUndo) {
                if (!editable.contains(tapToUndoString)) {
                    val tapToUndoSpannable =
                        SpannableString(tapToUndoString)
                    val tapToUndoSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            chatroomDetailAdapterListener?.blockMember(
                                position,
                                MemberBlockState.MEMBER_UNBLOCKED.value
                            )
                        }
                    }
                    tapToUndoSpannable.setSpan(
                        tapToUndoSpan,
                        0,
                        tapToUndoString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    editable.append(tapToUndoSpannable)
                }
            } else {
                if (editable.contains(tapToUndoString)) {
                    editable.removeSuffix(tapToUndoString)
                }
            }

            LinkifyCompat.addLinks(tvAction, Linkify.ALL)
            tvAction.setLinkTextColor(
                ContextCompat.getColor(
                    tvAction.context,
                    R.color.white
                )
            )
            tvAction.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}