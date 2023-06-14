package com.likeminds.chatmm.homefeed.view.adapter.databinder

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.util.ChatroomUtil
import com.likeminds.chatmm.databinding.ItemFollowedChatRoomBinding
import com.likeminds.chatmm.homefeed.model.ChatViewData
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapter
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.UserPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.membertagging.MemberTaggingDecoder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM

class FollowedChatroomViewDataBinder(
    val userPreferences: UserPreferences,
    val sdkPreferences: SDKPreferences,
    private val homeAdapterListener: HomeFeedAdapter.HomeFeedAdapterListener
) : ViewDataBinder<ItemFollowedChatRoomBinding, BaseViewType>() {

    companion object {
        private const val LAST_CONVERSATION_MAX_COUNT = 250
        private const val MAX_UNSEEN_CONVERSATION = 99
    }

    override val viewType: Int
        get() = ITEM_HOME_CHAT_ROOM

    override fun createBinder(parent: ViewGroup): ItemFollowedChatRoomBinding {
        val itemChatBinding = ItemFollowedChatRoomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        setRootClick(itemChatBinding)
        return itemChatBinding
    }

    private fun setRootClick(binding: ItemFollowedChatRoomBinding) {
        binding.root.setOnClickListener {
            val chatViewData = binding.chatViewData ?: return@setOnClickListener
            homeAdapterListener.onChatRoomClicked(chatViewData)
        }
    }

    override fun bindData(
        binding: ItemFollowedChatRoomBinding,
        data: BaseViewType,
        position: Int
    ) {
        binding.apply {
            chatViewData = data as ChatViewData
            hideBottomLine = data.isLastItem
            showUnseenCount = data.unseenConversationCount > 0

            ViewUtils.setChatroomImage(
                data.chatroomImageUrl,
                ivChatRoom,
                onChatroomImagePresent = {
                    ivChatRoom.show()
                    layoutImageGroup.cardViewGroupImages.hide()
                },
                onChatroomImageNotPresent = {
                    ivChatRoom.hide()
                    layoutImageGroup.cardViewGroupImages.show()
                    // todo:
//                    MemberImageUtil.showGroupedImages(
//                        layoutImageGroup,
//                        data.chatroom.memberViewData,
//                        data.members
//                    )
                })

            //Show unseen count
            tvUnseenCount.text =
                if (data.unseenConversationCount > MAX_UNSEEN_CONVERSATION) {
                    root.context.getString(R.string.max_two_digit_number)
                } else {
                    data.unseenConversationCount.toString()
                }

            //Set the chatroom type icon
            if (data.chatTypeDrawableId != null) {
                tvChatroomName.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, data.chatTypeDrawableId!!, 0
                )
            } else {
                tvChatroomName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            //secret chatroom lock icon
            val isSecretChatroom = data.chatroom.isSecret
            val hideSecretChatroomLockIcon = sdkPreferences.getHideSecretChatroomLockIcon()

            if (isSecretChatroom == true) {
                if (hideSecretChatroomLockIcon) {
                    ivSecretChatroom.hide()
                } else {
                    ivSecretChatroom.show()
                }
            } else {
                ivSecretChatroom.hide()
            }

            val lastConversation = data.lastConversation
            if (lastConversation?.deletedBy == null) {
                if (lastConversation != null) {
                    //Last conversation's chatroom preview was deleted
                    tvLastConversationMemberName.visibility = View.GONE
                    tvLastConversationAttachment.visibility = View.GONE
                    tvLastConversation.setTypeface(
                        tvLastConversation.typeface,
                        Typeface.ITALIC
                    )
                    tvLastConversation.text =
                        root.context.getString(R.string.chatroom_was_deleted)
                    //If last conversation exists and it is not deleted
                    tvLastConversation.setTypeface(
                        tvLastConversation.typeface,
                        Typeface.NORMAL
                    )
                    tvLastConversationMemberName.visibility = View.VISIBLE
                    tvLastConversationMemberName.text =
                        data.lastConversationMemberName

                    val spannableStringBuilder =
                        ChatroomUtil.getHomeScreenAttachmentData(
                            root.context,
                            lastConversation
                        ).first
                    if (spannableStringBuilder.isNotEmpty()) {
                        tvLastConversationAttachment.setText(
                            spannableStringBuilder,
                            TextView.BufferType.SPANNABLE
                        )
                        tvLastConversationAttachment.visibility = View.VISIBLE
                    } else {
                        tvLastConversationAttachment.visibility = View.GONE
                    }
                    val lastConversationAnswer = data.lastConversationText
                    if (lastConversationAnswer != null) {
                        tvLastConversation.text = if (
                            lastConversationAnswer.length > LAST_CONVERSATION_MAX_COUNT
                        ) {
                            lastConversationAnswer.substring(
                                0,
                                LAST_CONVERSATION_MAX_COUNT
                            )
                        } else {
                            lastConversationAnswer
                        }
                    }
                } else {
                    //No name and attachment to show here
                    tvLastConversationMemberName.visibility = View.GONE
                    tvLastConversationAttachment.visibility = View.GONE

                    tvLastConversation.setTypeface(
                        tvLastConversation.typeface,
                        Typeface.NORMAL
                    )
                    val chatroomTitle = data.chatroom.title
                    if (chatroomTitle.isNotBlank()) {
                        tvLastConversation.text = chatroomTitle
                    }
                }
                MemberTaggingDecoder.decode(
                    tvLastConversation,
                    tvLastConversation.text.toString(),
                    false,
                    LMBranding.getTextLinkColor()
                )
            } else {
                //Last conversation was deleted
                tvLastConversationMemberName.visibility = View.GONE
                tvLastConversationAttachment.visibility = View.GONE
                tvLastConversation.setTypeface(
                    tvLastConversation.typeface,
                    Typeface.ITALIC
                )
                tvLastConversation.text = ChatroomUtil.getDeletedMessage(
                    root.context,
                    lastConversation,
                    userPreferences.getMemberId()
                )
            }
        }
    }
}