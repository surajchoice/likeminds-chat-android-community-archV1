package com.likeminds.chatmm.report.view

import android.util.Log
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.DialogReportSuccessBinding
import com.likeminds.chatmm.utils.customview.BaseDialogFragment

class ReportSuccessDialog(private val type: String) :
    BaseDialogFragment<DialogReportSuccessBinding>() {

    override fun getViewBinding(): DialogReportSuccessBinding {
        return DialogReportSuccessBinding.inflate(layoutInflater)
    }

    override val cancellable: Boolean
        get() = true

    override val margin: Int
        get() = 30

    companion object {
        const val TAG = "com.likeminds.chatmm.report.view.ReportSuccessDialog"
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
    }

    private fun initView() {
        //set header and sub header as per [type] received in constructor
        Log.d(SDKApplication.LOG_TAG, "reports success opened")
        binding.apply {
            tvReportedHeader.text = getString(R.string.lm_chat_s_is_reported_for_review, type)
            tvReportSubHeader.text = getString(
                R.string.lm_chat_our_team_will_look_into_your_feedback_and_will_take_appropriate_action_on_this_s,
                type
            )

            btnOk.setOnClickListener {
                Log.d(SDKApplication.LOG_TAG, "reports success dismissed")
                dismiss()
            }
        }
    }
}