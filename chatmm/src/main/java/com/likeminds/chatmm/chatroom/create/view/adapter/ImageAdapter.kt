package com.likeminds.chatmm.chatroom.create.view.adapter

import com.likeminds.chatmm.chatroom.create.view.adapter.databinder.SmallAudioViewDataBinder
import com.likeminds.chatmm.chatroom.create.view.adapter.databinder.SmallDocumentViewDataBinder
import com.likeminds.chatmm.chatroom.create.view.adapter.databinder.SmallMediaViewDataBinder
import com.likeminds.chatmm.media.model.SmallMediaViewData
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ImageAdapter constructor(
    val listener: ImageAdapterListener,
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(3)

        val smallMediaViewDataBinder = SmallMediaViewDataBinder(listener)
        viewDataBinders.add(smallMediaViewDataBinder)

        val smallDocumentViewDataBinder = SmallDocumentViewDataBinder(listener)
        viewDataBinders.add(smallDocumentViewDataBinder)

        val smallAudioViewDataBinder = SmallAudioViewDataBinder(listener)
        viewDataBinders.add(smallAudioViewDataBinder)
        return viewDataBinders
    }
}

interface ImageAdapterListener {
    fun mediaSelected(position: Int, smallMediaViewData: SmallMediaViewData)
    fun onAddMediaClicked() {}
}