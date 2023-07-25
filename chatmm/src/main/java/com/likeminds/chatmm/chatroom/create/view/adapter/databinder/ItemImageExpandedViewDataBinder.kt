package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemImageExpandedBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.*

class ItemImageExpandedViewDataBinder :
    ViewDataBinder<ItemImageExpandedBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_IMAGE_EXPANDED

    override fun createBinder(parent: ViewGroup): ItemImageExpandedBinding {
        val inflater = LayoutInflater.from(parent.context)
        val itemImageExpandedBinding =
            ItemImageExpandedBinding.inflate(inflater, parent, false)

        itemImageExpandedBinding.image.setOnClickListener {
            val attachmentViewData =
                itemImageExpandedBinding.attachmentViewData ?: return@setOnClickListener
            val position = itemImageExpandedBinding.position
            val imageUri = attachmentViewData.uri
            val arr = mutableListOf<MediaSwipeViewData>()
            val parentConversation = attachmentViewData.parentConversation

            if (attachmentViewData.attachments.isNullOrEmpty()) {
                arr.add(
                    MediaSwipeViewData.Builder()
                        .dynamicViewType(ITEM_IMAGE_SWIPE)
                        .uri(imageUri)
                        .thumbnail(attachmentViewData.thumbnail)
                        .title(attachmentViewData.title)
                        .subTitle(attachmentViewData.subTitle)
                        .build()
                )
            } else {
                attachmentViewData.attachments.map { item ->
                    val viewType =
                        if (item.type == VIDEO) {
                            ITEM_VIDEO_SWIPE
                        } else {
                            ITEM_IMAGE_SWIPE
                        }
                    arr.add(
                        MediaSwipeViewData.Builder()
                            .dynamicViewType(viewType)
                            .index(item.index ?: 0)
                            .uri(item.uri)
                            .title(item.title)
                            .thumbnail(item.thumbnail)
                            .subTitle(item.subTitle).build()
                    )
                }
            }
            val extras = MediaExtras.Builder()
                .chatroomId(parentConversation?.chatroomId)
                .conversationId(parentConversation?.id)
                .communityId(attachmentViewData.communityId)
                .mediaScreenType(MEDIA_HORIZONTAL_LIST_SCREEN)
                .medias(arr)
                .position(position).build()

            MediaActivity.startActivity(
                itemImageExpandedBinding.root.context,
                extras
            )
        }
        return itemImageExpandedBinding
    }

    override fun bindData(
        binding: ItemImageExpandedBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        binding.attachmentViewData = data
        binding.position = position
    }
}