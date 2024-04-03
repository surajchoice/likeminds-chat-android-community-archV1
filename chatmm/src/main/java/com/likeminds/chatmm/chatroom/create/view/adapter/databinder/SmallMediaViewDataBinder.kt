package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.*
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.create.view.adapter.ImageAdapterListener
import com.likeminds.chatmm.databinding.ItemMediaSmallBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_SMALL

class SmallMediaViewDataBinder constructor(private val imageAdapterListener: ImageAdapterListener) :
    ViewDataBinder<ItemMediaSmallBinding, SmallMediaViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_SMALL

    override fun createBinder(parent: ViewGroup): ItemMediaSmallBinding {
        val inflater = LayoutInflater.from(parent.context)
        return ItemMediaSmallBinding.inflate(inflater, parent, false)
    }

    override fun bindData(
        binding: ItemMediaSmallBinding,
        data: SmallMediaViewData,
        position: Int
    ) {
        binding.apply {
            smallMediaViewData = data
            initMedia(this, data.singleUriData)

            if (data.isSelected) {
                clImage.background = ContextCompat.getDrawable(
                    root.context,
                    R.drawable.lm_chat_background_transparent_turquoise_2
                )
            } else {
                clImage.background = ContextCompat.getDrawable(
                    root.context,
                    R.drawable.lm_chat_background_transparent
                )
            }
            ivImg.setOnClickListener {
                imageAdapterListener.mediaSelected(position, data)
            }
        }
    }

    private fun initMedia(binding: ItemMediaSmallBinding, singleUriData: SingleUriData?) {
        binding.apply {
            if (singleUriData == null) {
                return
            }
            if (singleUriData.thumbnailUri != null) {
                ImageBindingUtil.loadImage(ivImg, singleUriData.thumbnailUri.toString())
            } else {
                ImageBindingUtil.loadImage(ivImg, singleUriData.uri.toString())
            }
            if (singleUriData.fileType == VIDEO) {
                ivVideo.visibility = View.VISIBLE
            } else {
                ivVideo.visibility = View.GONE
            }
        }
    }
}