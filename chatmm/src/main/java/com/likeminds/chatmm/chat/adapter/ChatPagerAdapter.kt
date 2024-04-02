package com.likeminds.chatmm.chat.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.likeminds.chatmm.chat.model.LMChatExtras
import com.likeminds.chatmm.chat.view.LMChatFragment
import com.likeminds.chatmm.dm.view.DMFeedFragment
import com.likeminds.chatmm.homefeed.view.HomeFeedFragment

class ChatPagerAdapter(
    private val fragment: LMChatFragment,
    private val lmChatExtras: LMChatExtras
) : FragmentStateAdapter(fragment) {

    companion object {
        const val LIST_SIZE = 2
    }

    override fun getItemCount(): Int {
        return LIST_SIZE
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFeedFragment.getInstance(lmChatExtras)
            }

            1 -> {
                DMFeedFragment.getInstance(fragment.dmMeta)
            }

            else -> {
                throw IndexOutOfBoundsException()
            }
        }
    }
}