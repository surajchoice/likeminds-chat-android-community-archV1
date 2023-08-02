package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemVideoExpandedBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.ITEM_VIDEO_EXPANDED
import com.likeminds.chatmm.utils.model.ITEM_VIDEO_SWIPE

class ItemVideoExpandedViewDataBinder :
    ViewDataBinder<ItemVideoExpandedBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_VIDEO_EXPANDED

    override fun createBinder(parent: ViewGroup): ItemVideoExpandedBinding {
        val inflater = LayoutInflater.from(parent.context)
        val itemVideoExpandedBinding =
            ItemVideoExpandedBinding.inflate(inflater, parent, false)
        itemVideoExpandedBinding.ivImage.setOnClickListener {
            val attachmentViewData =
                itemVideoExpandedBinding.attachmentViewData ?: return@setOnClickListener
            val videoUri = attachmentViewData.uri
            val parentConversation = attachmentViewData.parentConversation

            val extras = MediaExtras.Builder()
                .chatroomId(parentConversation?.chatroomId)
                .conversationId(parentConversation?.id)
                .communityId(parentConversation?.communityId?.toIntOrNull())
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
                itemVideoExpandedBinding.root.context,
                extras
            )
        }
        return itemVideoExpandedBinding
    }

    override fun bindData(
        binding: ItemVideoExpandedBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        binding.apply {
            attachmentViewData = data
            this.position = position
            if (!data.thumbnail.isNullOrEmpty()) {
                ImageBindingUtil.loadImage(ivImage, data.thumbnail)
            } else {
                ImageBindingUtil.loadImage(
                    ivImage, data.uri.toString()
                )
            }
        }
    }
}
