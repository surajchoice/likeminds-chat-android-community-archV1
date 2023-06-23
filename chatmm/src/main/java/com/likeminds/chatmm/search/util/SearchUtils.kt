package com.likeminds.chatmm.search.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt

object SearchUtils {
    fun highlightMatchedText(
        str: SpannableStringBuilder,
        @ColorInt color: Int,
        startIndex: Int,
        endIndex: Int,
        applyBoldSpan: Boolean = true
    ) {
        if (applyBoldSpan) {
            str.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        str.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}