package com.likeminds.chatmm.search.view

import androidx.lifecycle.lifecycleScope
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity
import com.likeminds.chatmm.databinding.FragmentSearchBinding
import com.likeminds.chatmm.homefeed.model.ChatroomListShimmerViewData
import com.likeminds.chatmm.search.model.SearchChatroomHeaderViewData
import com.likeminds.chatmm.search.model.SearchChatroomTitleViewData
import com.likeminds.chatmm.search.model.SearchConversationViewData
import com.likeminds.chatmm.search.model.SingleShimmerViewData
import com.likeminds.chatmm.search.util.CustomSearchBar
import com.likeminds.chatmm.search.util.SearchScrollListener
import com.likeminds.chatmm.search.view.adapter.SearchAdapter
import com.likeminds.chatmm.search.view.adapter.SearchAdapterListener
import com.likeminds.chatmm.search.viewmodel.SearchViewModel
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseFragment
import javax.inject.Inject

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(),
    SearchAdapterListener {
    override fun getViewModelClass(): Class<SearchViewModel> {
        return SearchViewModel::class.java
    }

    override fun getViewBinding(): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().searchComponent()?.inject(this)
    }

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    private lateinit var mAdapter: SearchAdapter

    companion object {
        const val TAG = "SearchFragment"

        fun getInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    private val scrollListener = object : SearchScrollListener() {
        override fun onLoadMore() {
            showSingleShimmer()
            viewModel.fetchNextPage(false)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initSearchBar()
        initRecyclerView()
    }

    override fun observeData() {
        super.observeData()

        viewModel.keywordSearched.observe(viewLifecycleOwner) {
            mAdapter.clearAndNotify()
            showFullShimmer()
            viewModel.fetchNextPage(false)
        }

        viewModel.searchLiveData.observe(viewLifecycleOwner) { searchData ->
            if (searchData != null) {
                //if search results are present remove whole shimmer
                if (searchData.dataList.isNotEmpty()) {
                    removeShimmer()
                }

                //if pagination is disabled then, scroll listener will not call [loadMore()]
                if (!searchData.disablePagination) {
                    scrollListener.setBottomLoadingToTrue()
                }

                //add line break into the adapter to show chatrooms are loaded
                if (searchData.checkForSeparator && searchData.dataList.isNotEmpty()) {
                    addSeparatorIfRequired()
                }
                //add list to adapter
                mAdapter.addAll(searchData.dataList)

                //if next page is called add single shimmer
                if (searchData.dataList.size < SearchViewModel.PAGE_SIZE
                    && (viewModel.currentApi != SearchViewModel.API_SEARCH_UNFOLLOWED_CONVERSATIONS)
                ) {
                    showSingleShimmer()
                }
            } else {
                //if search is closed or search keyword is cleared then clear the list.
                mAdapter.clearAndNotify()
            }
        }

        viewModel.noUnfollowedConversationsFound.observe(viewLifecycleOwner) {
            if (viewModel.searchLiveData.value != null) {
                if (it.first && it.second == viewModel.keywordSearched.value) {
                    removeShimmer()
                    if (mAdapter.itemCount == 0) {
                        showNoResultsView()
                    }
                }
            }
        }
    }

    override fun onChatroomClicked(searchChatroomHeaderViewData: SearchChatroomHeaderViewData) {
        super.onChatroomClicked(searchChatroomHeaderViewData)
//        todo: analytics
//        viewModel.sendChatroomClickedEvent(
//            searchChatroomHeaderViewData.chatroom.id(),
//            searchChatroomHeaderViewData.community?.id()
//        )
        startActivity(
            ChatroomDetailActivity.getIntent(
                requireContext(), ChatroomDetailExtras.Builder()
                    .chatroomViewData(searchChatroomHeaderViewData.chatroom)
                    .chatroomId(searchChatroomHeaderViewData.chatroom.id)
//                    .communityId(searchChatroomTitleViewData.community?.id())
//                    .source(LMAnalytics.Sources.SOURCE_HOME_FEED)
                    .isFromSearchChatroom(true)
                    .build()
            )
        )
    }

    override fun onTitleClicked(searchChatroomTitleViewData: SearchChatroomTitleViewData) {
        super.onTitleClicked(searchChatroomTitleViewData)
        //        todo: analytics
//        viewModel.sendChatroomClickedEvent(
//            searchChatroomTitleViewData.chatroom.id(),
//            searchChatroomTitleViewData.community?.id()
//        )

        startActivity(
            ChatroomDetailActivity.getIntent(
                requireContext(), ChatroomDetailExtras.Builder()
                    .chatroomViewData(searchChatroomTitleViewData.chatroom)
                    .chatroomId(searchChatroomTitleViewData.chatroom.id)
//                    .communityId(searchChatroomTitleViewData.community?.id())
//                    .source(LMAnalytics.Sources.SOURCE_HOME_FEED)
                    .isFromSearchChatroom(true)
                    .scrollToExtremeTopForHighlightingTitle(true)
                    .build()
            )
        )
    }

    override fun onMessageClicked(searchConversationViewData: SearchConversationViewData) {
        super.onMessageClicked(searchConversationViewData)
        //        todo: analytics
//        viewModel.sendMessageClickedEvent(
//            searchConversationViewData.chatroom?.id(),
//            searchConversationViewData.community.id()
//        )

        startActivity(
            ChatroomDetailActivity.getIntent(
                requireContext(), ChatroomDetailExtras.Builder()
                    .chatroomViewData(searchConversationViewData.chatroom)
                    .chatroomId(searchConversationViewData.chatroom?.id ?: "")
//                    .communityId(searchConversationViewData.community.id())
                    .conversationId(searchConversationViewData.chatroomAnswer.id)
//                    .source(LMAnalytics.Sources.SOURCE_HOME_FEED)
                    .isFromSearchMessage(true)
                    .build()
            )
        )
    }

    private fun initSearchBar() {
        binding.searchBar.apply {
            this.initialize(lifecycleScope)
            //open search bar with animation
            post {
                openSearch()
            }

            //set search listener
            setSearchViewListener(
                object : CustomSearchBar.SearchViewListener {
                    override fun onSearchViewOpened() {
                        ViewUtils.showKeyboard(context)
                        viewModel.sendSearchIconClickedEvent()
                    }

                    override fun onSearchViewClosed() {
                        viewModel.setSearchedDataToNull()
                        viewModel.sendSearchClosedEvent()
                        requireActivity().finish()
                    }

                    override fun keywordEntered(keyword: String) {
                        viewModel.setKeyword(keyword)
                    }

                    override fun emptyKeywordEntered() {
                        viewModel.setSearchedDataToNull()
                    }

                    override fun crossClicked() {
                        viewModel.sendSearchCrossIconClickedEvent()
                        viewModel.setSearchedDataToNull()
                    }
                }
            )

            //start observing the search keywords
            observeSearchView(debounce = true)
        }
    }

    private fun initRecyclerView() {
        mAdapter = SearchAdapter(this, sdkPreferences)
        binding.rvSearch.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            addOnScrollListener(scrollListener)
        }
    }

    private fun showSingleShimmer() {
        val shimmer = mAdapter.items().firstOrNull {
            it is ChatroomListShimmerViewData || it is SingleShimmerViewData
        }
        if (shimmer == null) {
            mAdapter.add(viewModel.getSingleShimmerView())
        }
    }

    /**
     * [SearchViewModel.getRequiredSeparator] returns a pair of a boolean and a list
     * the boolean is used to remove the last element from the list, if it is a header type view
     * the list is added in the view
     * if separator already present will get an empty list
     * if headers not present only display content header messages
     * if header is present then display line break and content header
     */
    private fun addSeparatorIfRequired() {
        val separator = viewModel.getRequiredSeparator(mAdapter.items())
        if (separator.first) {
            val index = mAdapter.items().indexOfLast { it is SearchChatroomHeaderViewData }
            mAdapter.removeIndex(index)
        }
        mAdapter.addAll(separator.second)
    }

    private fun showFullShimmer() {
        mAdapter.add(viewModel.getShimmerView())
    }

    private fun removeShimmer() {
        val index = mAdapter.items().indexOfFirst {
            it is ChatroomListShimmerViewData || it is SingleShimmerViewData
        }
        if (index.isValidIndex()) {
            mAdapter.removeIndex(index)
        }
    }

    private fun showNoResultsView() {
        mAdapter.add(viewModel.getNoSearchResultsView())
    }
}