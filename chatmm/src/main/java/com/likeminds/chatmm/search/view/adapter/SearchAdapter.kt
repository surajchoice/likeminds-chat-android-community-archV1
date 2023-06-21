package com.likeminds.chatmm.search.view.adapter

import com.likeminds.chatmm.homefeed.view.adapter.databinder.HomeChatroomListShimmerViewDataBinder
import com.likeminds.chatmm.search.model.SearchChatroomHeaderViewData
import com.likeminds.chatmm.search.model.SearchChatroomTitleViewData
import com.likeminds.chatmm.search.model.SearchConversationViewData
import com.likeminds.chatmm.search.view.adapter.databinder.*
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class SearchAdapter(
    private val listener: SearchAdapterListener,
    private val sdkPreferences: SDKPreferences
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): ArrayList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>()

        val searchChatroomHeaderViewDataBinder = SearchChatroomHeaderViewDataBinder(listener)
        viewDataBinders.add(searchChatroomHeaderViewDataBinder)

        val searchNoResultsViewDataBinder = SearchNoResultsViewDataBinder()
        viewDataBinders.add(searchNoResultsViewDataBinder)

        val homeChatroomListShimmerViewDataBinder = HomeChatroomListShimmerViewDataBinder()
        viewDataBinders.add(homeChatroomListShimmerViewDataBinder)

        val searchConversationViewDataBinder =
            SearchConversationViewDataBinder(listener, sdkPreferences)
        viewDataBinders.add(searchConversationViewDataBinder)

        val searchLineBreakViewDataBinder = SearchLineBreakViewDataBinder()
        viewDataBinders.add(searchLineBreakViewDataBinder)

        val searchContentHeaderBinder = SearchContentHeaderViewDataBinder()
        viewDataBinders.add(searchContentHeaderBinder)

        val searchChatroomTitleViewDataBinder =
            SearchChatroomTitleViewDataBinder(listener, sdkPreferences)
        viewDataBinders.add(searchChatroomTitleViewDataBinder)

        val singleShimmerViewDataBinder = SingleShimmerViewDataBinder()
        viewDataBinders.add(singleShimmerViewDataBinder)

        return viewDataBinders
    }
}

interface SearchAdapterListener {
    fun onChatroomClicked(searchChatroomHeaderViewData: SearchChatroomHeaderViewData) {}
    fun onMessageClicked(searchConversationViewData: SearchConversationViewData) {}
    fun onTitleClicked(searchChatroomTitleViewData: SearchChatroomTitleViewData) {}
}