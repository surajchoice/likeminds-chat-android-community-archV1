package com.likeminds.chatmm.dm.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.likeminds.chatmm.R
import com.likeminds.chatmm.databinding.DialogFragmentDmLimitExceededBinding
import com.likeminds.chatmm.dm.model.DMLimitExceededDialogExtras
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.customview.BaseDialogFragment

class DMLimitExceededDialogFragment :
    BaseDialogFragment<DialogFragmentDmLimitExceededBinding>() {

    companion object {
        private const val TAG = "DMLimitExceededDialogFragment"
        private const val DM_LIMIT_EXTRAS = "DM_LIMIT_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            dmLimitExceededDialogExtras: DMLimitExceededDialogExtras
        ) {
            DMLimitExceededDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DM_LIMIT_EXTRAS, dmLimitExceededDialogExtras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private lateinit var dmLimitExceededDialogExtras: DMLimitExceededDialogExtras

    override fun getViewBinding(): DialogFragmentDmLimitExceededBinding {
        return DialogFragmentDmLimitExceededBinding.inflate(layoutInflater)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        dmLimitExceededDialogExtras = ExtrasUtil.getParcelable(
            arguments,
            DM_LIMIT_EXTRAS,
            DMLimitExceededDialogExtras::class.java
        ) ?: throw ErrorUtil.emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.apply {
            tvOk.setOnClickListener {
                this@DMLimitExceededDialogFragment.dismiss()
            }

            val newRequestDMString =
                TimeUtil.getRelativeTimeInString(
                    System.currentTimeMillis(),
                    dmLimitExceededDialogExtras.newRequestDMTimestamp ?: 0
                )

            tvLimitInfo.text = getString(
                R.string.lm_chat_you_can_send_only_s_requests,
                dmLimitExceededDialogExtras.numberInDuration.toString(),
                dmLimitExceededDialogExtras.duration,
            )

            tvTryAgain.text = getString(R.string.lm_chat_try_again_in_s, newRequestDMString)
        }
    }
}