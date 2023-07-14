package com.likeminds.chatmm.polls.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.likeminds.chatmm.databinding.FragmentPollResultTabBinding
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.polls.adapter.PollResultTabFragmentAdapter
import com.likeminds.chatmm.polls.adapter.PollResultTabFragmentInterface
import com.likeminds.chatmm.polls.viewmodel.PollResultViewModel
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
    private var memberDirectoryLockDialog: AlertDialog? = null
    private var memberDirectoryLockEmailDialog: AlertDialog? = null

    override fun attachDagger() {
        super.attachDagger()
        // todo:
//        SDKApplication.getInstance().chatroomDetailComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(POLL_RESULT_TAB_EXTRA) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }

        extra = requireArguments().getParcelable(POLL_RESULT_TAB_EXTRA)!!
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

        // todo:
//        viewModel.memberStateData.observe(viewLifecycleOwner) {
//            if (it != null) {
//                viewModel.fetchPollParticipantsData(
//                    extra.pollViewData?.id,
//                    extra.communityId,
//                    conversationId = extra.conversationId
//                )
//            } else {
//                ViewUtils.showSomethingWentWrongToast(requireContext())
//            }
//        }
    }

    override fun isMemberOfCommunity(): Boolean {
        return viewModel.isMember() || viewModel.isAdmin()
    }

    override fun isEditable(): Boolean {
        return viewModel.isAdmin()
    }

    override fun showMemberProfile(memberViewData: MemberViewData, source: String) {
        // todo:
//        val extra = ProfileExtras.Builder()
//            .userId(memberViewData.id!!)
//            .communityId(memberViewData.communityId ?: "")
//            .source(source)
//            .build()
//        val intent = ProfileActivity.getIntent(
//            requireContext(),
//            extra
//        )
//        memberProfileResult.launch(intent)
    }

    private val memberProfileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.fetchPollParticipantsData(
                    extra.pollViewData?.id,
                    extra.communityId,
                    conversationId = extra.conversationId
                )
            }
        }

    override fun showMemberOptionsDialog(memberViewData: MemberViewData) {
        //todo
//        MemberOptionsDialog.newInstance(childFragmentManager, memberViewData)
    }

    private fun initData() {
        if (extra.pollViewData?.noVotes!! > 0) {
            binding.rvPollUsers.show()
            binding.layoutNoResponse.clLayout.hide()
            // todo:
//            viewModel.fetchMemberState()
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