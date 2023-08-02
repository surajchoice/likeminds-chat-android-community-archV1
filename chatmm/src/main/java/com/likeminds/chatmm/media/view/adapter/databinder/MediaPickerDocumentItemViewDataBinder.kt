package com.likeminds.chatmm.media.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemMediaPickerDocumentBinding
import com.likeminds.chatmm.media.model.MediaViewData
import com.likeminds.chatmm.media.util.MediaPickerDataBinderUtils
import com.likeminds.chatmm.media.util.MediaUtils
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.utils.DateUtil
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_DOCUMENT
import javax.inject.Inject

class MediaPickerDocumentItemViewDataBinder @Inject constructor(
    private val listener: MediaPickerAdapterListener,
) : ViewDataBinder<ItemMediaPickerDocumentBinding, MediaViewData>() {

    override val viewType: Int
        get() = ITEM_MEDIA_PICKER_DOCUMENT

    override fun createBinder(parent: ViewGroup): ItemMediaPickerDocumentBinding {
        val binding = ItemMediaPickerDocumentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            val mediaViewData = binding.mediaViewData ?: return@setOnClickListener
            val position = binding.position ?: return@setOnClickListener
            listener.onMediaItemClicked(mediaViewData, position)
        }
        return binding
    }

    override fun bindData(
        binding: ItemMediaPickerDocumentBinding, data: MediaViewData, position: Int,
    ) {
        binding.apply {
            this.position = position
            mediaViewData = data
            isSelected = listener.isMediaSelected(data.uri.toString())

            if (data.filteredKeywords.isNullOrEmpty()) {
                tvDocumentName.text = data.mediaName
            } else {
                tvDocumentName.setText(
                    MediaPickerDataBinderUtils.getFilteredText(
                        data.mediaName ?: "",
                        data.filteredKeywords,
                        ContextCompat.getColor(binding.root.context, R.color.turquoise),
                    ), TextView.BufferType.SPANNABLE
                )
            }

            tvDocumentSize.text = MediaUtils.getFileSizeText(data.size)
            tvDocumentDate.text = DateUtil.createDateFormat("dd/MM/yy", data.date)
        }
    }
}