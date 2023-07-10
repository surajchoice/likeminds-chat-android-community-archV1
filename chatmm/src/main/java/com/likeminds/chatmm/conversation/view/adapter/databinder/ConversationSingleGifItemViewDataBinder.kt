package com.likeminds.chatmm.conversation.view.adapter.databinder

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.util.ChatroomConversationItemViewDataBinderUtil
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationSingleGifBinding
import com.likeminds.chatmm.media.model.MEDIA_HORIZONTAL_LIST_SCREEN
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.utils.ProgressHelper
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_CONVERSATION_SINGLE_GIF
import com.likeminds.chatmm.utils.model.ITEM_IMAGE_SWIPE

internal class ConversationSingleGifItemViewDataBinder constructor(
    private val sdkPreferences: SDKPreferences,
//    private val messageReactionsPreferences: MessageReactionsPreferences,
    private val adapterListener: ChatroomDetailAdapterListener,
) : ViewDataBinder<ItemConversationSingleGifBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_CONVERSATION_SINGLE_GIF

    override fun createBinder(parent: ViewGroup): ItemConversationSingleGifBinding {
        val binding = ItemConversationSingleGifBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        initRootClick(binding)
        return binding
    }

    override fun bindData(
        binding: ItemConversationSingleGifBinding,
        data: BaseViewType,
        position: Int,
    ) {
        binding.apply {
            buttonColor = LMBranding.getButtonsColor()
            viewReply.buttonColor = LMBranding.getButtonsColor()
            conversation = data as ConversationViewData
            itemPosition = position
            ChatroomConversationItemViewDataBinderUtil.initConversationBubbleView(
                clConversationRoot,
                clConversationBubble,
                memberImage,
                tvConversationMemberName,
                tvCustomTitle,
                tvCustomTitleDot,
                data.memberViewData,
                sdkPreferences.getMemberId(),
                adapterListener,
                position,
                conversationViewData = data,
                imageViewStatus = ivConversationStatus,
                imageViewFailed = ivConversationFailed
            )

            if (data.deletedBy != null) {
                clImage.visibility = View.GONE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleDeletedTextView(
                    tvConversation,
                    tvDeleteMessage,
                    sdkPreferences.getMemberId(),
                    conversationViewData = data
                )
            } else {
                clImage.visibility = View.VISIBLE
                ChatroomConversationItemViewDataBinderUtil.initConversationBubbleTextView(
                    tvConversation,
                    data.answer,
                    position,
                    createdAt = data.createdAt,
                    conversation = data,
                    adapterListener = adapterListener,
                    tvDeleteMessage = binding.tvDeleteMessage
                )
                initSingleGifView(binding, data)
            }

//            ChatroomConversationItemViewDataBinderUtil.initReactionButton(
//                ivAddReaction,
//                data,
//                sdkPreferences.getMemberId()
//            )

            ChatroomConversationItemViewDataBinderUtil.initProgress(binding.tvProgress, data)

            ChatroomConversationItemViewDataBinderUtil.initTimeAndStatus(
                tvTime,
                sdkPreferences.getMemberId(),
                data.createdAt,
                data.answer.isEmpty() && data.deletedBy == null,
                imageViewStatus = ivConversationStatus,
                conversation = data
            )

            ChatroomConversationItemViewDataBinderUtil.initReplyView(
                viewReply,
                sdkPreferences.getMemberId(),
                data.replyConversation,
                data.replyChatroomId,
                adapterListener,
                itemPosition = position,
                conversation = data
            )

            ChatroomConversationItemViewDataBinderUtil.initSelectionAnimation(
                viewSelectionAnimation,
                position,
                adapterListener
            )

            ChatroomConversationItemViewDataBinderUtil.initReportView(
                ivReport,
                sdkPreferences.getMemberId(),
                adapterListener,
                conversationViewData = data
            )

            val viewList = listOf(
                root,
                memberImage,
                tvConversation,
                ivSingleImage,
                viewReply.root,
                ivReport
            )
            isSelected =
                ChatroomConversationItemViewDataBinderUtil.initConversationSelection(
                    root,
                    viewList,
                    data,
                    position,
                    adapterListener
                )

//            val messageReactionsGridViewData = ChatroomUtil.getMessageReactionsGrid(data)
//
//            ChatroomConversationItemViewDataBinderUtil.initMessageReactionGridView(
//                messageReactionsGridViewData,
//                clConversationRoot,
//                clConversationBubble,
//                messageReactionsGridLayout,
//                sdkPreferences.getMemberId(),
//                adapterListener,
//                data
//            )
//            val isReactionHintShown =
//                ChatroomConversationItemViewDataBinderUtil.isReactionHintViewShown(
//                    data.isLastItem,
//                    messageReactionsPreferences.getHasUserReactedOnce(),
//                    messageReactionsPreferences.getNoOfTimesHintShown(),
//                    messageReactionsPreferences.getTotalNoOfHintsAllowed(),
//                    tvDoubleTap,
//                    data.memberViewData,
//                    sdkPreferences.getMemberId(),
//                    clConversationRoot,
//                    clConversationBubble
//                )
//            if (isReactionHintShown) {
//                adapterListener.messageReactionHintShown()
//            }
        }
    }

    private fun initSingleGifView(
        binding: ItemConversationSingleGifBinding,
        conversation: ConversationViewData,
    ) {
        binding.apply {
            if (!conversation.attachments.isNullOrEmpty()) {
                val attachment = conversation.attachments.firstOrNull()
                if (attachment != null) {
                    ProgressHelper.hideProgress(binding.progressBar)
                    ivSingleImage.visibility = View.VISIBLE
                    loadGifThumbnail(ivSingleImage, attachment)

                    ChatroomConversationItemViewDataBinderUtil.initImageAspectRatio(
                        clImage,
                        ivSingleImage,
                        attachment
                    )

                    val uploadData =
                        ChatroomConversationItemViewDataBinderUtil.initUploadMediaAction(
                            viewMediaUploadingActions,
                            conversation = conversation,
                            listener = adapterListener
                        )

                    if (uploadData.second) {
                        viewGif.visibility = View.GONE
                    } else {
                        viewGif.visibility = View.VISIBLE
                    }

                    if (uploadData.first != null) {
                        adapterListener.observeMediaUpload(
                            uploadData.first!!, conversation
                        )
                    }
                } else {
                    ivSingleImage.visibility = View.GONE
                    ProgressHelper.showProgress(progressBar, false)
                }
            } else {
                ivSingleImage.visibility = View.GONE
                ProgressHelper.hideProgress(progressBar)
            }
        }
    }

    private fun loadGifThumbnail(imageView: ImageView, attachment: AttachmentViewData?) {
        if (attachment != null) {
            val thumbnail = attachment.thumbnail
            if (!thumbnail.isNullOrEmpty()) {
                ImageBindingUtil.loadImage(
                    imageView,
                    thumbnail,
                    placeholder = R.drawable.image_placeholder,
                    cornerRadius = 10
                )
            } else {
                ImageBindingUtil.loadFirstFrameOfGif(
                    imageView,
                    attachment.uri,
                    placeholder = R.drawable.image_placeholder,
                    cornerRadius = 10
                )
            }
        }
    }

    private fun initRootClick(binding: ItemConversationSingleGifBinding) {
        binding.apply {
            ivSingleImage.setOnClickListener {
                val actionsVisible = viewMediaUploadingActions.actionsVisible
                if (actionsVisible == true) return@setOnClickListener

                val conversation = conversation ?: return@setOnClickListener
                val itemPosition = itemPosition
                if (itemPosition != null && adapterListener.isSelectionEnabled()) {
                    adapterListener.onLongPressConversation(
                        conversation,
                        itemPosition,
                        LMAnalytics.Source.MESSAGE_REACTIONS_FROM_LONG_PRESS
                    )
                } else {
                    val imageUri = conversation.attachments?.firstOrNull()?.uri
                    if (viewGif.isVisible) {
                        createGifAnimation(this, imageUri)
                    } else {
                        showGifFullScreen(this, conversation, imageUri)
                    }
                }
            }

            ivAddReaction.setOnClickListener {
                val conversation = conversation ?: return@setOnClickListener
                val itemPosition = itemPosition
                if (itemPosition != null) {
                    adapterListener.onLongPressConversation(
                        conversation,
                        itemPosition,
                        LMAnalytics.Source.MESSAGE_REACTIONS_FROM_REACTION_BUTTON
                    )
                }
            }
        }
    }

    private fun showGifFullScreen(
        binding: ItemConversationSingleGifBinding,
        conversation: ConversationViewData,
        imageUri: Uri?,
    ) {
        val subTitle = "${conversation.date ?: ""}, ${conversation.createdAt ?: ""}"
        adapterListener.onScreenChanged()
        val extra = MediaExtras.Builder()
            .communityId(conversation.communityId?.toIntOrNull())
            .conversationId(conversation.id)
            .chatroomId(conversation.chatroomId)
            .mediaScreenType(MEDIA_HORIZONTAL_LIST_SCREEN)
            .medias(
                listOf(
                    MediaSwipeViewData.Builder()
                        .dynamicViewType(ITEM_IMAGE_SWIPE)
                        .uri(imageUri ?: Uri.EMPTY)
                        .type("gif")
                        .title(conversation.memberViewData.name ?: "")
                        .subTitle(subTitle)
                        .build()
                )
            )
            .build()
        MediaActivity.startActivity(
            binding.root.context,
            extra
        )
    }

    private fun createGifAnimation(
        binding: ItemConversationSingleGifBinding,
        imageUri: Uri?,
    ) {
        binding.viewGif.visibility = View.GONE
        Glide.with(binding.ivSingleImage).asGif().load(imageUri)
            .transform(CenterCrop(), RoundedCorners(ViewUtils.dpToPx(10)))
            .addListener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    if (resource != null) {
                        startGifAnimation(binding, resource)
                    }
                    return false
                }
            })
            .into(binding.ivSingleImage)
    }

    private fun startGifAnimation(
        binding: ItemConversationSingleGifBinding,
        resource: GifDrawable,
    ) {
        resource.setLoopCount(2)
        resource.registerAnimationCallback(object :
            Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                binding.viewGif.visibility = View.VISIBLE
                resource.clearAnimationCallbacks()
                loadGifThumbnail(
                    binding.ivSingleImage,
                    binding.conversation?.attachments?.firstOrNull()
                )
            }
        })
        resource.startFromFirstFrame()
    }
}
