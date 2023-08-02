package com.likeminds.chatmm.polls.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.likeminds.chatmm.polls.model.PollInfoData

class PollResultTabAdapter(
    fragment: Fragment,
    val communityId: String?,
    private val pollInfoData: PollInfoData,
    val conversationId: String?,
    val chatroomId: String?,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return pollInfoData.pollViewDataList?.size ?: 0
    }

    override fun createFragment(position: Int): Fragment {
        val extra = PollResultTabExtra.Builder()
            .chatroomId(chatroomId)
            .communityId(communityId)
            .conversationId(conversationId)
            .pollViewData(pollInfoData.pollViewDataList?.get(position))
            .build()
        return PollResultTabFragment.addFragment(extra)
    }
}