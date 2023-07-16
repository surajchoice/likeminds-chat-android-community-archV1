package com.likeminds.chatmm.polls.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.DialogPollVoteSubmitSuccessBinding
import com.likeminds.chatmm.utils.customview.BaseBottomSheetFragment

class PollVoteSubmitSuccessDialog :
    BaseBottomSheetFragment<DialogPollVoteSubmitSuccessBinding, Nothing>() {

    companion object {
        private const val TAG = "PollVoteSubmitSuccessDialog"
        private const val ARG_POLL_EXPIRE_TIME = "ARG_POLL_EXPIRE_TIME"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager, pollExpireTime: String?) =
            PollVoteSubmitSuccessDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_POLL_EXPIRE_TIME, pollExpireTime)
                }
            }.show(fragmentManager, TAG)
    }

    private var pollExpireTime: String? = null

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun getViewBinding(): DialogPollVoteSubmitSuccessBinding {
        return DialogPollVoteSubmitSuccessBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().pollsComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        arguments?.let {
            pollExpireTime = it.getString(ARG_POLL_EXPIRE_TIME)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.btnContinue.setOnClickListener {
            dismiss()
        }

        binding.tvVoteDate.text = String.format(
            getString(R.string.results_will_be_announced_when_voting_ends_on_s),
            pollExpireTime
        )
    }
}