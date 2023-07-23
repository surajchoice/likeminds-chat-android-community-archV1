package com.likeminds.chatmm.media.view.adapter.databinder

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemMediaPickerSingleBinding
import com.likeminds.chatmm.media.model.InternalMediaType
import com.likeminds.chatmm.media.model.MediaViewData
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_SINGLE
import javax.inject.Inject

class MediaPickerSingleItemViewDataBinder @Inject constructor(
    private val listener: MediaPickerAdapterListener,
) : ViewDataBinder<ItemMediaPickerSingleBinding, MediaViewData>() {

    private var glideRequestManager: RequestManager? = null
    private var placeHolderDrawable: ColorDrawable? = null

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_SINGLE

    override fun createBinder(parent: ViewGroup): ItemMediaPickerSingleBinding {
        val binding = ItemMediaPickerSingleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.apply {
            root.setOnClickListener {
                val mediaViewData = mediaViewData ?: return@setOnClickListener
                val position = position ?: return@setOnClickListener
                if (listener.isMediaSelectionEnabled()) {
                    listener.onMediaItemLongClicked(mediaViewData, position)
                } else {
                    listener.onMediaItemClicked(mediaViewData, position)
                }
            }
            root.setOnLongClickListener {
                if (listener.isMultiSelectionAllowed()) {
                    val mediaViewData = mediaViewData ?: return@setOnLongClickListener false
                    val position = position ?: return@setOnLongClickListener false
                    listener.onMediaItemLongClicked(mediaViewData, position)
                    return@setOnLongClickListener true
                } else {
                    return@setOnLongClickListener false
                }
            }
            glideRequestManager = Glide.with(root)
            placeHolderDrawable =
                ColorDrawable(ContextCompat.getColor(binding.root.context, R.color.bright_grey))
        }
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerSingleBinding,
        data: MediaViewData,
        position: Int
    ) {
        binding.apply {
            this.position = position
            mediaViewData = data
            isSelected = listener.isMediaSelected(data.uri.toString())

            glideRequestManager?.load(data.uri)
                ?.diskCacheStrategy(DiskCacheStrategy.NONE)
                ?.transition(DrawableTransitionOptions.withCrossFade())
                ?.placeholder(placeHolderDrawable)
                ?.error(placeHolderDrawable)
                ?.into(ivThumbnail)

            val showVideoIcon = InternalMediaType.isVideo(data.mediaType)
            ivFileTypeIcon.isVisible = showVideoIcon
            ivShadow.isVisible = showVideoIcon
        }
    }
}