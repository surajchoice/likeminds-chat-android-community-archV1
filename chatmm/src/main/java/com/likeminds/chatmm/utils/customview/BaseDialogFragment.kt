package com.likeminds.chatmm.utils.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.likeminds.chatmm.utils.ViewUtils

abstract class BaseDialogFragment<B : ViewBinding> : DialogFragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    protected abstract fun getViewBinding(): B

    protected open val cancellable = true
    protected open val margin = 16
    protected open val useSharedViewModel = false

    protected open val width = LinearLayout.LayoutParams.MATCH_PARENT
    protected open val height = LinearLayout.LayoutParams.WRAP_CONTENT

    protected open fun setUpViews() {}
    protected open fun observeData() {}
    protected open fun attachDagger() {}
    protected open fun receiveExtras() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachDagger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiveExtras()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawable(
            InsetDrawable(
                ColorDrawable(Color.TRANSPARENT),
                ViewUtils.dpToPx(margin)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = cancellable

        setUpViews()
        observeData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}