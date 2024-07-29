package com.likeminds.chatmm.member.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemDmAllMembersBinding
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.*
import com.likeminds.chatmm.member.view.adapter.CommunityMembersAdapterListener
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_COMMUNITY_MEMBER
import javax.inject.Inject

class CommunityMembersViewDataBinder @Inject constructor(
    private val listener: CommunityMembersAdapterListener,
    private val userPreferences: UserPreferences
) : ViewDataBinder<ItemDmAllMembersBinding, MemberViewData>() {

    override val viewType: Int
        get() = ITEM_COMMUNITY_MEMBER

    override fun createBinder(parent: ViewGroup): ItemDmAllMembersBinding {
        val binding = ItemDmAllMembersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    override fun bindData(binding: ItemDmAllMembersBinding, data: MemberViewData, position: Int) {
        binding.apply {
            memberViewData = data
            showCustomTitle = !data.customTitle.isNullOrEmpty()
                    && data.customTitle != binding.root.context.getString(R.string.lm_chat_member)
            MemberImageUtil.setImage(data.imageUrl, data.name, data.id, ivMemberImage)

            tvMemberName.text = MemberUtil.getMemberNameForDisplay(data, userPreferences.getUUID())

            val subtitle = data.sdkClientInfo.uuid

            tvSubtitle.apply {
                text = subtitle
                isVisible = subtitle.isNotEmpty()
            }
        }
    }

    private fun setListeners(binding: ItemDmAllMembersBinding) {
        binding.apply {
            root.setOnClickListener {
                val member = memberViewData ?: return@setOnClickListener
                listener.onMemberSelected(member)
            }
        }
    }
}