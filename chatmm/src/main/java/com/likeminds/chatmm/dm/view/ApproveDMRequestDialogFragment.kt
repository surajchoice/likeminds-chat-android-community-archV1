package com.likeminds.chatmm.dm.view

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.likeminds.chatmm.databinding.DialogFragmentApproveDmRequestBinding
import com.likeminds.chatmm.utils.customview.BaseDialogFragment

class ApproveDMRequestDialogFragment :
    BaseDialogFragment<DialogFragmentApproveDmRequestBinding>() {

    private lateinit var approveDMRequestDialogListener: ApproveDMRequestDialogListener

    companion object {
        private const val TAG = "AcceptDMRequestDialogFragment"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager
        ) {
            ApproveDMRequestDialogFragment().show(supportFragmentManager, TAG)
        }
    }

    override fun getViewBinding(): DialogFragmentApproveDmRequestBinding {
        return DialogFragmentApproveDmRequestBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            approveDMRequestDialogListener = parentFragment as ApproveDMRequestDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement ApproveDMRequestDialogListener interface")
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeListeners()
    }

    private fun initializeListeners() {
        binding.apply {
            tvCancel.setOnClickListener {
                this@ApproveDMRequestDialogFragment.dismiss()
            }

            tvApprove.setOnClickListener {
                approveDMRequestDialogListener.approveDMRequest()
                this@ApproveDMRequestDialogFragment.dismiss()
            }
        }
    }
}

interface ApproveDMRequestDialogListener {
    fun approveDMRequest()
}