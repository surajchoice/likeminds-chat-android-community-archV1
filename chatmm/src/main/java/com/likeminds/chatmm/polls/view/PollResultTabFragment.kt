package com.likeminds.chatmm.polls.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.FragmentPollResultTabBinding
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.polls.adapter.PollResultTabFragmentAdapter
import com.likeminds.chatmm.polls.adapter.PollResultTabFragmentInterface
import com.likeminds.chatmm.polls.viewmodel.PollResultViewModel
import com.likeminds.chatmm.utils.ExtrasUtil
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.BaseFragment
import javax.inject.Inject

class PollResultTabFragment :
    BaseFragment<FragmentPollResultTabBinding, PollResultViewModel>(),
    PollResultTabFragmentInterface {

    override fun getViewModelClass(): Class<PollResultViewModel> {
        return PollResultViewModel::class.java
    }

    override fun getViewBinding(): FragmentPollResultTabBinding {
        return FragmentPollResultTabBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var mAdapter: PollResultTabFragmentAdapter

    private lateinit var extra: PollResultTabExtra

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().pollsComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(POLL_RESULT_TAB_EXTRA) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        extra = ExtrasUtil.getParcelable(
            requireArguments(),
            POLL_RESULT_TAB_EXTRA,
            PollResultTabExtra::class.java
        ) ?: return
    }

    override fun setUpViews() {
        super.setUpViews()
        initData()
        initRecyclerView()
    }

    override fun observeData() {
        viewModel.memberList.observe(viewLifecycleOwner) {
            mAdapter.replace(it)
        }

        viewModel.memberState.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.fetchPollParticipantsData(
                    extra.pollViewData?.id,
                    extra.communityId,
                    conversationId = extra.conversationId
                )
            } else {
                ViewUtils.showSomethingWentWrongToast(requireContext())
            }
        }
    }

    override fun isMemberOfCommunity(): Boolean {
        return viewModel.isMember() || viewModel.isAdmin()
    }

    override fun isEditable(): Boolean {
        return viewModel.isAdmin()
    }

    override fun showMemberProfile(memberViewData: MemberViewData, source: String) {
        SDKApplication.getLikeMindsCallback()?.openProfile(memberViewData)
    }

    override fun showMemberOptionsDialog(memberViewData: MemberViewData) {
        //todo
//        MemberOptionsDialog.newInstance(childFragmentManager, memberViewData)
    }

    private fun initData() {
        if (extra.pollViewData?.noVotes!! > 0) {
            binding.rvPollUsers.show()
            binding.layoutNoResponse.clLayout.hide()
            viewModel.fetchMemberState()
        } else {
            binding.rvPollUsers.hide()
            binding.layoutNoResponse.clLayout.show()
        }
    }

    private fun initRecyclerView() {
        mAdapter = PollResultTabFragmentAdapter(userPreferences, this)
        binding.rvPollUsers.adapter = mAdapter
    }

    companion object {

        const val POLL_RESULT_TAB_EXTRA = "extra of Poll Result Tab"

        @JvmStatic
        fun addFragment(pollData: PollResultTabExtra?): PollResultTabFragment {
            val fragment = PollResultTabFragment()
            val bundle = Bundle()
            bundle.putParcelable(POLL_RESULT_TAB_EXTRA, pollData)
            fragment.arguments = bundle
            return fragment
        }
    }
}