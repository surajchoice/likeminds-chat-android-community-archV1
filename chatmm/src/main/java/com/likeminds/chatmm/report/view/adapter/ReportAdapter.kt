package com.likeminds.chatmm.report.view.adapter

import com.likeminds.chatmm.report.model.ReportTagViewData
import com.likeminds.chatmm.report.view.adapter.databinder.ReportTagItemViewDataBinder
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class ReportAdapter(
    private val listener: ReportAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>()
        viewDataBinders.add(ReportTagItemViewDataBinder(listener))
        return viewDataBinders
    }
}

interface ReportAdapterListener {
    fun reportTagSelected(reportTagViewData: ReportTagViewData) {}
}