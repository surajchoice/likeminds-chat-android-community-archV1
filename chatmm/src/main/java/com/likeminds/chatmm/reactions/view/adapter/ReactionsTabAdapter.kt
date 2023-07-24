package com.likeminds.chatmm.reactions.view.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.reactions.view.ReactionListFragment

class ReactionsTabAdapter(
    fragment: Fragment,
    private val itemsCount: Int,
    private val allReactions: MutableList<ReactionViewData>,
    private val reactionsHashmap: Map<String, List<ReactionViewData>>?,
    private val conversationId: String
) : FragmentStateAdapter(fragment) {

    companion object {
        const val ARG_COUNT = "ARG_COUNT"
        const val ARG_LIST = "ARG_LIST"
        const val ARG_LIST_ALL = "ARG_LIST_ALL"
        const val ARG_REACTION = "ARG_REACTION"
        const val ARG_CONVERSATION_ID = "ARG_CONVERSATION_ID"
    }

    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment {
        val fragment = ReactionListFragment.getInstance()
        fragment.arguments = Bundle().apply {
            if (position == 0) {
                putParcelableArrayList(
                    ARG_LIST_ALL,
                    allReactions as ArrayList<ReactionViewData>
                )
                putInt(ARG_COUNT, allReactions.size)
            } else {
                val listOfReactions = reactionsHashmap?.keys?.toList()
                val members =
                    reactionsHashmap?.get(listOfReactions?.get(position - 1)) as ArrayList<ReactionViewData>
                putParcelableArrayList(
                    ARG_LIST,
                    members
                )
                putString(ARG_REACTION, listOfReactions?.get(position - 1))
                putInt(ARG_COUNT, members.size)
            }
            putString(ARG_CONVERSATION_ID, conversationId)
        }
        return fragment
    }
}
