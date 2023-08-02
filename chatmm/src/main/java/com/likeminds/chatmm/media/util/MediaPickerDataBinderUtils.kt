package com.likeminds.chatmm.media.util

import android.text.SpannableStringBuilder
import com.likeminds.chatmm.search.util.SearchUtils

class MediaPickerDataBinderUtils {
    companion object {
        fun getFilteredText(
            text: String, keywords: List<String>, color: Int
        ): SpannableStringBuilder {
            val stringBuilder = SpannableStringBuilder(text)
            keywords.forEach { keyword ->
                if (stringBuilder.startsWith(keyword, ignoreCase = true)) {
                    SearchUtils.highlightMatchedText(
                        stringBuilder, color, startIndex = 0,
                        endIndex = keyword.length, applyBoldSpan = false
                    )
                }
                if (stringBuilder.contains(keyword, ignoreCase = true)) {
                    var startIndex = 0
                    while (startIndex != -1) {
                        startIndex = stringBuilder.indexOf(keyword, startIndex, ignoreCase = true)
                        if (startIndex != -1) {
                            SearchUtils.highlightMatchedText(
                                stringBuilder, color, startIndex,
                                endIndex = startIndex + keyword.length, applyBoldSpan = false
                            )
                            startIndex += keyword.length
                        }
                    }
                }
            }
            return stringBuilder
        }
    }
}