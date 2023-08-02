package com.likeminds.chatmm.media.view.adapter

import com.likeminds.chatmm.media.model.MediaFolderViewData
import com.likeminds.chatmm.media.model.MediaViewData
import com.likeminds.chatmm.media.view.adapter.databinder.*
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class MediaPickerAdapter constructor(
    val listener: MediaPickerAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(6)

        val mediaPickerFolderItemViewDataBinder = MediaPickerFolderItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerFolderItemViewDataBinder)

        val mediaPickerHeaderItemViewDataBinder = MediaPickerHeaderItemViewDataBinder()
        viewDataBinders.add(mediaPickerHeaderItemViewDataBinder)

        val mediaPickerSingleItemViewDataBinder = MediaPickerSingleItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerSingleItemViewDataBinder)

        val mediaPickerBrowseItemViewDataBinder = MediaPickerBrowseItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerBrowseItemViewDataBinder)

        val mediaPickerDocumentItemViewDataBinder = MediaPickerDocumentItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerDocumentItemViewDataBinder)

        val mediaPickerAudioItemViewDataBinder = MediaPickerAudioItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerAudioItemViewDataBinder)
        return viewDataBinders
    }

}

interface MediaPickerAdapterListener {
    fun onFolderClicked(folderData: MediaFolderViewData) {}
    fun onMediaItemClicked(mediaViewData: MediaViewData, itemPosition: Int) {}
    fun onMediaItemLongClicked(mediaViewData: MediaViewData, itemPosition: Int) {}
    fun isMediaSelectionEnabled(): Boolean {
        return false
    }

    fun isMediaSelected(key: String): Boolean {
        return false
    }

    fun browseDocumentClicked() {}
    fun isMultiSelectionAllowed(): Boolean {
        return false
    }

    fun onAudioActionClicked(mediaViewData: MediaViewData, itemPosition: Int) {}
}