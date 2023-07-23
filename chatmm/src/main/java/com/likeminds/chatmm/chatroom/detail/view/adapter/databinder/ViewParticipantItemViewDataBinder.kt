package com.likeminds.chatmm.chatroom.detail.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.chatmm.chatroom.detail.view.adapter.ViewParticipantsAdapterListener
import com.likeminds.chatmm.databinding.ItemViewParticipantBinding
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_VIEW_PARTICIPANTS

class ViewParticipantItemViewDataBinder constructor(
    private val adapterListener: ViewParticipantsAdapterListener,
) : ViewDataBinder<ItemViewParticipantBinding, MemberViewData>() {

    override val viewType: Int
        get() = ITEM_VIEW_PARTICIPANTS

    override fun createBinder(parent: ViewGroup): ItemViewParticipantBinding {
        val binding = ItemViewParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    private fun setListeners(binding: ItemViewParticipantBinding) {
        binding.root.setOnClickListener {
            val memberViewData = binding.memberViewData ?: return@setOnClickListener
            adapterListener.onMemberClick(memberViewData)
        }
    }

    override fun bindData(
        binding: ItemViewParticipantBinding,
        data: MemberViewData,
        position: Int,
    ) {
        binding.apply {
            memberViewData = data
            this.position = position
            MemberImageUtil.setImage(
                data.imageUrl,
                data.name,
                data.id,
                binding.ivMember,
                showRoundImage = true
            )
        }
    }
}
