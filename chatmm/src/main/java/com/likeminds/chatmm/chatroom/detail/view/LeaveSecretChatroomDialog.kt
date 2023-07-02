package com.likeminds.chatmm.chatroom.detail.view

import com.likeminds.chatmm.databinding.DialogLeaveSecretChatroomBinding
import com.likeminds.chatmm.utils.customview.BaseDialogFragment


/**
 * Dialog used to show when user is leaving a secret chatroom
 **/
class LeaveSecretChatroomDialog(private val listener: LeaveSecretChatroomDialogListener) :
    BaseDialogFragment<DialogLeaveSecretChatroomBinding>() {

    override fun getViewBinding(): DialogLeaveSecretChatroomBinding {
        return DialogLeaveSecretChatroomBinding.inflate(layoutInflater)
    }

    override val cancellable: Boolean
        get() = true

    override val margin: Int
        get() = 50

    companion object {
        const val TAG = "LeaveSecretChatroomDialog"
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.btnPositive.setOnClickListener {
            listener.positiveButtonClick()
            dismiss()
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
        }
    }
}

interface LeaveSecretChatroomDialogListener {
    fun positiveButtonClick()
}