package com.likeminds.chatmm.polls.adapter.databinders

import android.view.*
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.likeminds.chatmm.LMAnalytics
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.ItemMemberDirectoryListBinding
import com.likeminds.chatmm.member.model.MemberState
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.*
import com.likeminds.chatmm.polls.adapter.PollResultTabFragmentInterface
import com.likeminds.chatmm.polls.util.DialogUtil
import com.likeminds.chatmm.utils.ViewUtils.blurText
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_POLL_RESULT_USER

internal class PollResultUserItemViewDataBinder constructor(
    private val userPreferences: UserPreferences,
    private val adapterListener: PollResultTabFragmentInterface
) : ViewDataBinder<ItemMemberDirectoryListBinding, MemberViewData>() {

    override val viewType: Int
        get() = ITEM_POLL_RESULT_USER

    override fun createBinder(parent: ViewGroup): ItemMemberDirectoryListBinding {
        val binding = ItemMemberDirectoryListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    override fun bindData(
        binding: ItemMemberDirectoryListBinding,
        data: MemberViewData,
        position: Int,
    ) {
        binding.apply {
            memberViewData = data
            showCustomTitle =
                !data.customTitle.isNullOrEmpty() && data.customTitle != root.context.getString(R.string.member)

            MemberImageUtil.setImage(
                data.imageUrl,
                data.name,
                data.sdkClientInfo.uuid,
                memberImage
            )

            tvMemberName.text =
                MemberUtil.getMemberNameForDisplay(data, userPreferences.getUUID())

            tvSubtitle.text = data.customIntroText ?: data.memberSince

            when {
                data.isGuest == true || MemberState.isMemberSkipPrivateLink(data.state) -> {
                    setSubTitleTextColor(binding, R.color.lm_chat_turquoise)
                }
                else -> {
                    setSubTitleTextColor(binding, R.color.lm_chat_grey)
                }
            }

            if (!adapterListener.isMemberOfCommunity()) {
                tvSubtitle.blurText()
            } else {
                tvSubtitle.paint.maskFilter = null
            }

            if (MemberState.isPendingMember(data.state)) {
                groupPendingView.visibility = View.VISIBLE
                ivMore.visibility = View.GONE
                if (adapterListener.isEditable()) {
                    groupRequestButtons.visibility = View.VISIBLE
                } else {
                    groupRequestButtons.visibility = View.GONE
                }
            } else {
                groupPendingView.visibility = View.GONE
                groupRequestButtons.visibility = View.GONE
                if (adapterListener.isEditable()) {
                    if (!data.listOfMenu.isNullOrEmpty()) {
                        ivMore.visibility = View.VISIBLE
                    } else {
                        ivMore.visibility = View.GONE
                    }
                } else {
                    ivMore.visibility = View.GONE
                }
            }
        }
    }

    private fun setSubTitleTextColor(
        binding: ItemMemberDirectoryListBinding,
        @ColorRes colorResId: Int,
    ) {
        binding.tvSubtitle.setTextColor(
            ResourcesCompat.getColor(binding.root.context.resources, colorResId, null)
        )
    }

    private fun setListeners(binding: ItemMemberDirectoryListBinding) {
        binding.root.setOnClickListener {
            val memberViewData =
                binding.memberViewData ?: return@setOnClickListener
            if (memberViewData.customClickText != null) {
                DialogUtil.showProfileNotExist(it.context, memberViewData.customClickText)
            } else {
                adapterListener.showMemberProfile(
                    memberViewData,
                    LMAnalytics.Source.POLL_RESULT
                )
            }
        }

        binding.ivMore.setOnClickListener {
            val memberViewData =
                binding.memberViewData ?: return@setOnClickListener
            adapterListener.showMemberOptionsDialog(memberViewData)
        }
    }
}