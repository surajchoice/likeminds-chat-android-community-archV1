package com.likeminds.chatmm.polls.view

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.databinding.DialogAddPollOptionBinding
import com.likeminds.chatmm.polls.model.AddPollOptionExtras
import com.likeminds.chatmm.polls.util.AddPollOptionListener
import com.likeminds.chatmm.utils.ViewUtils.fetchColor
import com.likeminds.chatmm.utils.customview.BaseBottomSheetFragment

class AddPollOptionDialog :
    BaseBottomSheetFragment<DialogAddPollOptionBinding, Nothing>() {

    companion object {
        private const val TAG = "AddPollOptionDialog"

        private const val ARG_ADD_POLL_OPTION_EXTRAS = "ARG_ADD_POLL_OPTION_EXTRAS"

        fun newInstance(
            fragmentManager: FragmentManager,
            addPollOptionExtras: AddPollOptionExtras,
        ) = AddPollOptionDialog().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ADD_POLL_OPTION_EXTRAS, addPollOptionExtras)
            }
        }.show(fragmentManager, TAG)
    }

    private var addPollOptionListener: AddPollOptionListener? = null
    private lateinit var addPollOptionExtras: AddPollOptionExtras

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun getViewBinding(): DialogAddPollOptionBinding {
        return DialogAddPollOptionBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        // todo:
//        SDKApplication.getInstance().chatroomDetailComponent()?.inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            addPollOptionListener = parentFragment as AddPollOptionListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement AddPollOptionListener interface")
        }
    }

    override fun receiveExtras() {
        super.receiveExtras()
        val addPollOptionExtras =
            arguments?.getParcelable(ARG_ADD_POLL_OPTION_EXTRAS) as? AddPollOptionExtras
                ?: throw IllegalArgumentException("Calling activity must pass arguments")
        this.addPollOptionExtras = addPollOptionExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        setupSubmitButton()
        initializeListeners()
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.backgroundTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                LMBranding.getButtonsColor(),
                requireContext().fetchColor(R.color.black_14)
            )
        )
    }

    private fun initializeListeners() {
        binding.apply {
            imageViewCancel.setOnClickListener {
                dismiss()
            }

            etOption.addTextChangedListener { editable ->
                editable?.let {
                    val enteredEmail = it.toString().trim()
                    binding.btnSubmit.isEnabled = enteredEmail.isNotEmpty()
                }
            }

            btnSubmit.setOnClickListener {
                val data = addPollOptionExtras.toBuilder()
                    .pollOptionText(etOption.text.toString()).build()
                addPollOptionListener?.newPollOptionEntered(data)
                dismiss()
            }
        }
    }
}