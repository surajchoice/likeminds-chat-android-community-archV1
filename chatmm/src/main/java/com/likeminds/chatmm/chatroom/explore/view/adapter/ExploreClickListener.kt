package com.likeminds.chatmm.chatroom.explore.view.adapter

import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData

internal interface ExploreClickListener {
    fun onChatroomClick(exploreViewData: ExploreViewData, position: Int)
    fun onJoinClick(follow: Boolean, position: Int, exploreViewData: ExploreViewData)
}