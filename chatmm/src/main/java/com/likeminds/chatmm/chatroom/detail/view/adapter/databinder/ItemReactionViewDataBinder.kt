package com.likeminds.chatmm.chatroom.detail.view.adapter.databinder

import android.view.*
import com.likeminds.chatmm.chatroom.detail.view.adapter.ReactionAdapterListener
import com.likeminds.chatmm.databinding.ItemReactionListBinding
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_MESSAGE_REACTION

class ItemReactionViewDataBinder constructor(
    private val userPreferences: UserPreferences,
    private val reactionAdapterListener: ReactionAdapterListener
) : ViewDataBinder<ItemReactionListBinding, ReactionViewData>() {

    override val viewType: Int
        get() = ITEM_MESSAGE_REACTION

    override fun createBinder(parent: ViewGroup): ItemReactionListBinding {
        return ItemReactionListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemReactionListBinding,
        data: ReactionViewData,
        position: Int
    ) {
        binding.apply {
            reactionViewData = data
            tvMemberName.text = data.memberViewData.name
            tvReactionRedHeart.text = data.reaction
            val memberId = userPreferences.getMemberId()
            if (memberId == data.memberViewData.id) {
                tvRemoveReaction.visibility = View.VISIBLE
            } else {
                tvRemoveReaction.visibility = View.GONE
            }
            MemberImageUtil.setImage(
                data.memberViewData.imageUrl,
                data.memberViewData.name,
                data.memberViewData.id,
                memberImage
            )
            tvRemoveReaction.setOnClickListener {
                if (data.conversationId != null) {
                    reactionAdapterListener.removeReaction(data.conversationId.toString())
                }
                if (data.chatroomId != null) {
                    reactionAdapterListener.removeChatroomReaction()
                }
            }
        }
    }
}