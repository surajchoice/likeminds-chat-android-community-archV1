package com.likeminds.chatmm.search.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.databinding.LayoutNoSearchResultsBinding
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_NO_SEARCH_RESULTS_VIEW

class SearchNoResultsViewDataBinder :
    ViewDataBinder<LayoutNoSearchResultsBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_NO_SEARCH_RESULTS_VIEW

    override fun createBinder(parent: ViewGroup): LayoutNoSearchResultsBinding {
        return LayoutNoSearchResultsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LayoutNoSearchResultsBinding,
        data: BaseViewType,
        position: Int
    ) {
    }
}