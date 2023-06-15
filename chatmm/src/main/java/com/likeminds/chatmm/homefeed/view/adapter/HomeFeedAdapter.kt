package com.likeminds.chatmm.homefeed.view.adapter

import com.likeminds.chatmm.homefeed.model.ChatViewData
import com.likeminds.chatmm.homefeed.view.adapter.databinder.*
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.BaseViewType

class HomeFeedAdapter constructor(
    val sdkPreferences: SDKPreferences,
    val listener: HomeFeedAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(8)

        val followedChatroomViewDataBinder =
            FollowedChatroomViewDataBinder(sdkPreferences, listener)
        viewDataBinders.add(followedChatroomViewDataBinder)

        val emptyViewDataBinder = EmptyViewDataBinder()
        viewDataBinders.add(emptyViewDataBinder)

        val contentHeaderViewDataBinder = ContentHeaderViewDataBinder()
        viewDataBinders.add(contentHeaderViewDataBinder)

        val homeLineBreakViewDataBinder = HomeLineBreakViewDataBinder()
        viewDataBinders.add(homeLineBreakViewDataBinder)

        val homeBlankSpaceViewDataBinder = HomeBlankSpaceItemViewDataBinder()
        viewDataBinders.add(homeBlankSpaceViewDataBinder)

        val homeChatroomListShimmerViewDataBinder = HomeChatroomListShimmerViewDataBinder()
        viewDataBinders.add(homeChatroomListShimmerViewDataBinder)

        val homeFeedViewDataBinder = HomeFeedViewDataBinder(listener)
        viewDataBinders.add(homeFeedViewDataBinder)
        return viewDataBinders
    }

    interface HomeFeedAdapterListener {
        fun onChatRoomClicked(chatViewData: ChatViewData)
        fun homeFeedClicked()
    }
}