package com.likeminds.chatmm.report.view.adapter.databinder

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.databinding.ItemReportTagBinding
import com.likeminds.chatmm.report.model.ReportTagViewData
import com.likeminds.chatmm.report.view.adapter.ReportAdapterListener
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.ViewDataBinder
import com.likeminds.chatmm.utils.model.ITEM_REPORT_TAG

class ReportTagItemViewDataBinder constructor(
    private val listener: ReportAdapterListener
) : ViewDataBinder<ItemReportTagBinding, ReportTagViewData>() {

    override val viewType: Int
        get() = ITEM_REPORT_TAG

    override fun createBinder(parent: ViewGroup): ItemReportTagBinding {
        val binding = ItemReportTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        setListeners(binding)
        return binding
    }

    // sets tag background to the buttons color
    private fun setTagBackground(binding: ItemReportTagBinding) {
        val drawable = binding.tvReportTag.background as GradientDrawable
        drawable.mutate()
        val width = ViewUtils.dpToPx(1)

        binding.apply {
            if (reportTagViewData?.isSelected == true) {
                drawable.setStroke(width, LMBranding.getButtonsColor())
            } else {
                drawable.setStroke(
                    width,
                    ContextCompat.getColor(root.context, R.color.brown_grey)
                )
            }
        }
    }

    override fun bindData(
        binding: ItemReportTagBinding,
        data: ReportTagViewData,
        position: Int
    ) {
        binding.apply {
            reportTagViewData = data
            buttonColor = LMBranding.getButtonsColor()
            setTagBackground(this)
        }
    }

    // sets click listener to handle selected report tag
    private fun setListeners(binding: ItemReportTagBinding) {
        binding.apply {
            tvReportTag.setOnClickListener {
                val reportTagViewData = reportTagViewData ?: return@setOnClickListener
                listener.reportTagSelected(reportTagViewData)
            }
        }
    }
}