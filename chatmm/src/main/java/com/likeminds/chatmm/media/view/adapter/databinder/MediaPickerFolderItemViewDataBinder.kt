package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemMediaPickerFolderBinding
import com.likeminds.chatmm.media.model.MediaFolderType
import com.likeminds.chatmm.media.model.MediaFolderViewData
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_FOLDER
import javax.inject.Inject

class MediaPickerFolderItemViewDataBinder @Inject constructor(
    private val listener: MediaPickerAdapterListener,
) : ViewDataBinder<ItemMediaPickerFolderBinding, MediaFolderViewData>() {

    private var glideRequestManager: RequestManager? = null

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_FOLDER

    override fun createBinder(parent: ViewGroup): ItemMediaPickerFolderBinding {
        val binding = ItemMediaPickerFolderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            val folderData = binding.folderData ?: return@setOnClickListener
            listener.onFolderClicked(folderData)
        }
        glideRequestManager = Glide.with(binding.root)
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerFolderBinding, data: MediaFolderViewData, position: Int,
    ) {
        binding.apply {
            folderData = data
            ivFolderIcon.setImageResource(getFolderIcon(data.folderType))

            glideRequestManager?.load(data.thumbnailUri)
                ?.diskCacheStrategy(DiskCacheStrategy.NONE)
                ?.transition(DrawableTransitionOptions.withCrossFade())
                ?.into(ivThumbnail)
        }
    }

    private fun getFolderIcon(folderType: MediaFolderType): Int {
        return when (folderType) {
            MediaFolderType.CAMERA -> R.drawable.lm_chat_ic_camera_white
            MediaFolderType.LIKEMINDS -> R.drawable.lm_chat_ic_likeminds_logo
            else -> R.drawable.lm_chat_ic_folder
        }
    }
}