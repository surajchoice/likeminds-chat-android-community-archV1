package com.likeminds.chatmm.overflowmenu.view.adapter

import com.likeminds.chatmm.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class OverflowMenuAdapter(private val overflowMenuAdapterListener: OverflowMenuAdapterListener) :
    BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, BaseViewType>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, BaseViewType>>(1)
        viewDataBinders.add(OverflowMenuItemViewDataBinder(overflowMenuAdapterListener))
        return viewDataBinders
    }

}

fun interface OverflowMenuAdapterListener {
    fun onMenuItemClicked(menu: OverflowMenuItemViewData)
}