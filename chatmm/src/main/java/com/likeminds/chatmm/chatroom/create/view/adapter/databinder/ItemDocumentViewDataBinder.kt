package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.create.view.adapter.ChatroomItemAdapterListener
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemDocumentBinding
import com.likeminds.chatmm.media.util.MediaUtils
import com.likeminds.chatmm.utils.AndroidUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_DOCUMENT

class ItemDocumentViewDataBinder constructor(
    private val adapterListener: ChatroomItemAdapterListener?,
) : ViewDataBinder<ItemDocumentBinding, AttachmentViewData>() {

    override val viewType: Int
        get() = ITEM_DOCUMENT

    override fun createBinder(parent: ViewGroup): ItemDocumentBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDocumentBinding.inflate(inflater, parent, false)
        setListeners(binding)
        return binding
    }

    override fun bindData(
        binding: ItemDocumentBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        binding.apply {
            val attachment = data
            this.attachment = attachment
            this.position = position
            parentConversation = attachment.parentConversation
            parentChatRoom = attachment.parentChatroom
            parentViewItemPosition = attachment.parentViewItemPosition

            tvDocumentName.text = if (attachment.name.isNullOrEmpty()) {
                "Document"
            } else {
                attachment.name
            }

            tvMeta1.hide()
            viewMetaDot1.hide()
            tvMeta2.hide()
            viewMetaDot2.hide()
            tvMeta3.hide()
            if (attachment.meta != null) {
                val noOfPage = attachment.meta.numberOfPage ?: 0
                val size = attachment.meta.size ?: 0
                val mediaType = attachment.type
                if (noOfPage > 0) {
                    tvMeta1.show()
                    tvMeta1.text = binding.root.context.getString(
                        R.string.placeholder_pages, noOfPage
                    )
                }
                if (size > 0) {
                    tvMeta2.show()
                    tvMeta2.text = MediaUtils.getFileSizeText(size)
                    if (tvMeta1.isVisible) {
                        viewMetaDot1.show()
                    }
                }
                if (mediaType.isNotEmpty() && (tvMeta1.isVisible || tvMeta2.isVisible)) {
                    tvMeta3.show()
                    tvMeta3.text = mediaType
                    viewMetaDot2.show()
                }
            }
        }
    }

    private fun setListeners(binding: ItemDocumentBinding) {
        binding.apply {
            root.setOnLongClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnLongClickListener true

                val parentConversation = this.parentConversation
                val parentChatRoom = this.parentChatRoom
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

            root.setOnClickListener {
                val actionsVisible = adapterListener?.isMediaActionVisible()
                if (actionsVisible == true) return@setOnClickListener

                val parentConversation = this.parentConversation
                val parentChatRoom = this.parentChatRoom
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
                        else -> openPdf(this)
                    }
                } else {
                    openPdf(this)
                }
            }
        }
    }

    private fun openPdf(binding: ItemDocumentBinding) {
        val uri = binding.attachment?.uri ?: return
        adapterListener?.onScreenChanged()
        AndroidUtils.startDocumentViewer(binding.root.context, uri)
    }

}