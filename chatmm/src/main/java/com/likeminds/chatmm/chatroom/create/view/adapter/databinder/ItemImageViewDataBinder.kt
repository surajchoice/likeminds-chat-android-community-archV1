package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.*
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemImageBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.*

class ItemImageViewDataBinder constructor(
    private val adapterListener: ChatroomItemAdapterListener?,
) : ViewDataBinder<ItemImageBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_IMAGE

    override fun createBinder(parent: ViewGroup): ItemImageBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(inflater, parent, false)

        binding.apply {
            image.setOnLongClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnLongClickListener true

                val parentConversation = this.parentConversation
                val parentChatRoom = this.parentChatroom
                val parentPosition = this.parentViewItemPosition
                if (parentPosition != null) {
                    if (parentConversation != null) {
                        adapterListener?.onLongPressConversation(parentConversation, parentPosition)
                    } else if (parentChatRoom != null) {
                        adapterListener?.onLongPressChatRoom(parentChatRoom, parentPosition)
                    }
                }

                return@setOnLongClickListener true
            }

            image.setOnClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnClickListener

                val parentConversation = this.parentConversation
                val parentChatRoom = this.parentChatroom
                val parentPosition = this.parentViewItemPosition

                if (parentPosition != null && adapterListener?.isSelectionEnabled() == true) {
                    when {
                        parentConversation != null ->
                            adapterListener.onLongPressConversation(
                                parentConversation,
                                parentPosition
                            )
                        parentChatRoom != null ->
                            adapterListener.onLongPressChatRoom(parentChatRoom, parentPosition)
                        else ->
                            showImageFullScreen(this)
                    }
                } else {
                    showImageFullScreen(this)
                }
            }
        }
        return binding
    }

    override fun bindData(
        binding: ItemImageBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        binding.apply {
            this.attachmentViewData = data
            this.position = position
            parentConversation = data.parentConversation
            parentChatroom = data.parentChatroom
            parentViewItemPosition = data.parentViewItemPosition
            initImage(this, data)
            initImagesLeftView(this, data)
        }
    }

    private fun initImage(
        binding: ItemImageBinding,
        attachmentViewData: AttachmentViewData,
    ) {
        binding.apply {
            val imageUri = attachmentViewData.uri
            image.visibility = View.VISIBLE
            ImageBindingUtil.loadImage(
                image,
                imageUri.toString(),
                placeholder = R.drawable.lm_chat_image_placeholder,
                cornerRadius = 8
            )
        }
    }

    private fun initImagesLeftView(
        binding: ItemImageBinding,
        attachmentViewData: AttachmentViewData,
    ) {
        binding.apply {
            val mediaLeft = attachmentViewData.mediaLeft
            if (mediaLeft != null) {
                ivImage.background =
                    ContextCompat.getDrawable(root.context, R.drawable.lm_chat_background_black60_8)
                tvLeft.visibility = View.VISIBLE
                tvLeft.text = "+ $mediaLeft"
            } else {
                ivImage.background =
                    ContextCompat.getDrawable(root.context, R.drawable.lm_chat_background_transparent)
                tvLeft.visibility = View.GONE
            }
        }
    }

    private fun showImageFullScreen(binding: ItemImageBinding) {
        val attachmentViewData = binding.attachmentViewData ?: return
        val position = binding.position
        val mediaLeft = attachmentViewData.mediaLeft
        val attachmentsCount = attachmentViewData.attachments?.size ?: 0
        val parentConversation = attachmentViewData.parentConversation
        val communityId = parentConversation?.communityId?.toIntOrNull()

        if (mediaLeft != null || attachmentsCount == 3 || attachmentsCount > 4) {
            adapterListener?.onScreenChanged()
            val extra = MediaExtras.Builder()
                .communityId(communityId)
                .conversationId(parentConversation?.id)
                .chatroomId(parentConversation?.chatroomId)
                .mediaScreenType(MEDIA_VERTICAL_LIST_SCREEN)
                .attachments(attachmentViewData.attachments)
                .title(attachmentViewData.title)
                .position(position)
                .subtitle(attachmentViewData.subTitle)
                .build()
            MediaActivity.startActivity(
                binding.root.context,
                extra
            )
        } else {
            val imageUri = attachmentViewData.uri
            val medias = mutableListOf<MediaSwipeViewData>()
            if (attachmentViewData.attachments.isNullOrEmpty()) {
                medias.add(
                    MediaSwipeViewData.Builder()
                        .dynamicViewType(ITEM_IMAGE_SWIPE).uri(imageUri)
                        .thumbnail(attachmentViewData.thumbnail)
                        .title(attachmentViewData.title)
                        .subTitle(attachmentViewData.subTitle)
                        .build()
                )
            } else {
                attachmentViewData.attachments.forEach { item ->
                    val viewType = if (item.type == VIDEO) {
                        ITEM_VIDEO_SWIPE
                    } else {
                        ITEM_IMAGE_SWIPE
                    }
                    medias.add(
                        MediaSwipeViewData.Builder()
                            .dynamicViewType(viewType).uri(item.uri)
                            .thumbnail(item.thumbnail)
                            .index(item.index ?: 0)
                            .title(item.title)
                            .subTitle(item.subTitle).build()
                    )
                }
            }
            adapterListener?.onScreenChanged()
            val extras = MediaExtras.Builder()
                .conversationId(parentConversation?.id)
                .chatroomId(parentConversation?.chatroomId)
                .mediaScreenType(MEDIA_HORIZONTAL_LIST_SCREEN)
                .communityId(communityId)
                .medias(medias)
                .position(position)
                .build()
            MediaActivity.startActivity(
                binding.root.context,
                extras
            )
        }
    }
}