package com.likeminds.chatmm.chatroom.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.create.view.adapter.ImageAdapterListener
import com.likeminds.chatmm.databinding.ItemDocumentSmallBinding
import com.likeminds.chatmm.media.model.SmallMediaViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.model.ITEM_DOCUMENT_SMALL

class SmallDocumentViewDataBinder constructor(private val imageAdapterListener: ImageAdapterListener) :
    ViewDataBinder<ItemDocumentSmallBinding, SmallMediaViewData>() {

    override val viewType: Int
        get() = ITEM_DOCUMENT_SMALL

    override fun createBinder(parent: ViewGroup): ItemDocumentSmallBinding {
        return ItemDocumentSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(
        binding: ItemDocumentSmallBinding,
        data: SmallMediaViewData,
        position: Int
    ) {
        binding.apply {
            smallMediaViewData = data

            ImageBindingUtil.loadImage(
                ivIcon,
                data.singleUriData.thumbnailUri,
                R.drawable.lm_chat_ic_pdf
            )
            if (data.isSelected) {
                constraintLayout.background = ContextCompat.getDrawable(
                    root.context,
                    R.drawable.lm_chat_background_transparent_turquoise_2
                )
            } else {
                constraintLayout.background = ContextCompat.getDrawable(
                    root.context,
                    R.drawable.lm_chat_background_transparent
                )
            }
            root.setOnClickListener {
                imageAdapterListener.mediaSelected(position, data)
            }
        }
    }
}