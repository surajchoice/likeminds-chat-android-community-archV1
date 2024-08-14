package com.likeminds.chatmm.buysellwidget.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.likeminds.chatmm.databinding.DialogBuySellCustomWidgetBinding

//class BuySellCustomWidgetDialog :
//    BaseBottomSheetFragment<DialogBuySellCustomWidgetBinding, BuySellCustomWidgetViewModel>() {
//
//    companion object {
//        private const val TAG = "BuySellCustomWidgetDialog"
//
//        @JvmStatic
//        fun show(
//            fragmentManager: FragmentManager,
//        ) {
//            BuySellCustomWidgetDialog().show(fragmentManager, TAG)
//        }
//    }
//
//    override fun getViewModelClass(): Class<BuySellCustomWidgetViewModel>? {
//        return BuySellCustomWidgetViewModel::class.java
//    }
//
//    override fun getViewBinding(): DialogBuySellCustomWidgetBinding {
//        return DialogBuySellCustomWidgetBinding.inflate(layoutInflater)
//    }
//}

class BuySellCustomWidgetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogBuySellCustomWidgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using DataBindingUtil
        _binding = DialogBuySellCustomWidgetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}