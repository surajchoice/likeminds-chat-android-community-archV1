package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemVideoBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.*

class ItemVideoViewDataBinder constructor(
    private val adapterListener: ChatroomItemAdapterListener?,
) : ViewDataBinder<ItemVideoBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_VIDEO

    override fun createBinder(parent: ViewGroup): ItemVideoBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoBinding.inflate(inflater, parent, false)

        binding.apply {

            image.setOnLongClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnLongClickListener true

                val parentConversation = this.parentConversation
                val parentChatroom = this.parentChatroom
                val parentPosition = this.parentViewItemPosition
                if (parentPosition != null) {
                    if (parentConversation != null) {
                        adapterListener?.onLongPressConversation(parentConversation, parentPosition)
                    } else if (parentChatroom != null) {
                        adapterListener?.onLongPressChatRoom(parentChatroom, parentPosition)
                    }
                }

                return@setOnLongClickListener true
            }

            image.setOnClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnClickListener

                val parentConversation = this.parentConversation
                val parentChatroom = this.parentChatroom
                val parentPosition = this.parentViewItemPosition

                if (parentPosition != null && adapterListener?.isSelectionEnabled() == true) {
                    when {
                        parentConversation != null ->
                            adapterListener.onLongPressConversation(
                                parentConversation,
                                parentPosition
                            )
                        parentChatroom != null ->
                            adapterListener.onLongPressChatRoom(parentChatroom, parentPosition)
                        else ->
                            showVideoFullScreen(binding)
                    }
                } else {
                    showVideoFullScreen(binding)
                }
            }
        }
        return binding
    }

    override fun bindData(binding: ItemVideoBinding, data: BaseViewType, position: Int) {
        binding.apply {
            val attachmentViewData = data as AttachmentViewData
            this.attachmentViewData = attachmentViewData
            this.position = position
            this.parentConversation = attachmentViewData.parentConversation
            this.parentChatroom = attachmentViewData.parentChatroom
            this.parentViewItemPosition = attachmentViewData.parentViewItemPosition
            initVideoThumbnail(this, attachmentViewData)
            initVideoLeftView(this, attachmentViewData)
        }
    }

    private fun initVideoThumbnail(
        binding: ItemVideoBinding,
        attachmentViewData: AttachmentViewData,
    ) {
        binding.apply {
            val videoUri = attachmentViewData.uri
            image.show()

            if (!attachmentViewData.thumbnail.isNullOrEmpty()) {
                ImageBindingUtil.loadImage(
                    image,
                    attachmentViewData.thumbnail.toString()
                )
            } else {
                ImageBindingUtil.loadImage(image, videoUri.toString())
            }
        }
    }

    private fun initVideoLeftView(
        binding: ItemVideoBinding,
        attachmentViewData: AttachmentViewData,
    ) {
        binding.apply {
            val mediaLeft = attachmentViewData.mediaLeft
            if (mediaLeft != null) {
                ivImage.background =
                    ContextCompat.getDrawable(root.context, R.drawable.lm_chat_background_black60_8)
                tvLeft.show()
                tvLeft.text = "+ $mediaLeft"
            } else {
                ivImage.background =
                    ContextCompat.getDrawable(root.context, R.drawable.lm_chat_background_transparent)
                tvLeft.hide()
            }
        }
    }

    private fun showVideoFullScreen(binding: ItemVideoBinding) {
        binding.apply {
            val attachmentViewData = this.attachmentViewData ?: return
            val position = this.position
            val mediaLeft = attachmentViewData.mediaLeft
            val attachmentsCount = attachmentViewData.attachments?.size ?: 0
            val parentConversation = attachmentViewData.parentConversation
            val communityId = parentConversation?.communityId

            if (mediaLeft != null || attachmentsCount == 3 || attachmentsCount > 4) {
                adapterListener?.onScreenChanged()
                val extras = MediaExtras.Builder()
                    .mediaScreenType(MEDIA_VERTICAL_LIST_SCREEN)
                    .chatroomId(parentConversation?.chatroomId)
                    .communityId(communityId?.toIntOrNull())
                    .conversationId(parentConversation?.id)
                    .attachments(attachmentViewData.attachments)
                    .title(attachmentViewData.title)
                    .position(position)
                    .subtitle(attachmentViewData.subTitle)
                    .build()
                MediaActivity.startActivity(
                    root.context,
                    extras
                )
            } else {
                val videoUri = attachmentViewData.uri
                adapterListener?.onScreenChanged()
                val extras = MediaExtras.Builder()
                    .chatroomId(parentConversation?.chatroomId)
                    .conversationId(parentConversation?.id)
                    .communityId(communityId?.toInt())
                    .mediaScreenType(MEDIA_VIDEO_PLAY_SCREEN)
                    .medias(
                        listOf(
                            MediaSwipeViewData.Builder()
                                .dynamicViewType(ITEM_VIDEO_SWIPE)
                                .uri(videoUri)
                                .thumbnail(attachmentViewData.thumbnail)
                                .title(attachmentViewData.title)
                                .subTitle(attachmentViewData.subTitle)
                                .build()
                        )
                    )
                    .build()
                MediaActivity.startActivity(
                    root.context,
                    extras
                )
            }
        }
    }
}