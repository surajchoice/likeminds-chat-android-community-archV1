package com.likeminds.chatmm.reactions.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.view.adapter.ReactionAdapter
import com.likeminds.chatmm.chatroom.detail.view.adapter.ReactionAdapterListener
import com.likeminds.chatmm.databinding.FragmentReactionListBinding
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.reactions.view.adapter.ReactionsTabAdapter
import com.likeminds.chatmm.utils.model.BaseViewType
import javax.inject.Inject

class ReactionListFragment : Fragment(), ReactionAdapterListener {

    companion object {
        const val TAG = "ReactionListFragment"
        fun getInstance(): ReactionListFragment {
            return ReactionListFragment()
        }
    }

    private lateinit var reactionListBinding: FragmentReactionListBinding

    private var reactionRemovedFragmentListener:
            ReactionRemovedFragmentListener? = null

    lateinit var adapter: ReactionAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            reactionRemovedFragmentListener =
                parentFragment as ReactionRemovedFragmentListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement MessageReactionRemovedFragmentListener interface")
        }
        SDKApplication.getInstance().reactionsComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reactionListBinding = FragmentReactionListBinding.inflate(inflater)
        initRecyclerView()
        return reactionListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ReactionsTabAdapter.ARG_COUNT) }
        adapter.setItems(getReactionsList())
    }

    private fun initRecyclerView() {
        adapter = ReactionAdapter(userPreferences, this)
        val layoutManager = LinearLayoutManager(context)
        reactionListBinding.rvReactions.apply {
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            adapter = adapter
        }
    }

    private fun getAllReactionsList(): ArrayList<ReactionViewData>? {
        arguments?.takeIf { it.containsKey(ReactionsTabAdapter.ARG_LIST_ALL) }?.apply {
            return getParcelableArrayList(ReactionsTabAdapter.ARG_LIST_ALL)
        }
        return null
    }

    private fun getMembersList(): ArrayList<ReactionViewData>? {
        arguments?.takeIf { it.containsKey(ReactionsTabAdapter.ARG_LIST) }?.apply {
            return getParcelableArrayList(ReactionsTabAdapter.ARG_LIST)
        }
        return null
    }

    private fun getReaction(): String? {
        arguments?.takeIf { it.containsKey(ReactionsTabAdapter.ARG_REACTION) }?.apply {
            return getString(ReactionsTabAdapter.ARG_REACTION)
        }
        return null
    }

    private fun getConversationId(): String? {
        arguments?.takeIf { it.containsKey(ReactionsTabAdapter.ARG_CONVERSATION_ID) }?.apply {
            return getString(ReactionsTabAdapter.ARG_CONVERSATION_ID)
        }
        return null
    }

    private fun getReactionsList(): List<BaseViewType> {
        val dataList = mutableListOf<BaseViewType>()
        if (!getMembersList().isNullOrEmpty() && !getReaction().isNullOrEmpty() && !getConversationId().isNullOrEmpty()) {
            getMembersList()?.map {
                dataList.add(it)
            }
        }
        if (!getAllReactionsList().isNullOrEmpty()) {
            getAllReactionsList()?.map {
                dataList.add(it)
            }
        }
        return dataList
    }

    override fun removeReaction(conversationId: String) {
        reactionRemovedFragmentListener?.removedReaction(conversationId)
    }

    override fun removeChatroomReaction() {
        reactionRemovedFragmentListener?.removedChatroomReaction()
    }
}

interface ReactionRemovedFragmentListener {
    fun removedReaction(conversationId: String)
    fun removedChatroomReaction()
}