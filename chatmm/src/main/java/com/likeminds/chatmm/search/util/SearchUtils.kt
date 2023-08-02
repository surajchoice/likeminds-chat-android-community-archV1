package com.likeminds.chatmm.search.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt

object SearchUtils {
    fun getTrimmedText(
        conversation: String,
        keywords: List<String>,
        @ColorInt color: Int
    ): SpannableStringBuilder {
        val keyword = keywords[0]
        val trimmedText: String
        val ind: Int
        var wordPos = -1
        val listOfWords = conversation.split(" ").map { it.trim() }

        for (i in listOfWords.indices) {
            if (listOfWords[i].startsWith(keyword, ignoreCase = true)) {
                wordPos = i
                break
            }
        }
        if (wordPos <= 3) {
            trimmedText = conversation
        } else if (wordPos > 3 && wordPos < listOfWords.size - 5) {
            ind = conversation.indexOf(" $keyword", ignoreCase = true)
            val totalLen =
                listOfWords[wordPos - 3].length + listOfWords[wordPos - 2].length + listOfWords[wordPos - 1].length + 3
            trimmedText = "... " + conversation.substring(ind - totalLen)
        } else {
            var totalLen = 0
            for (i in listOfWords.size - 1 downTo listOfWords.size - 5) {
                totalLen += listOfWords[i].length
            }
            totalLen += 4
            trimmedText = if (listOfWords.size == 5) {
                conversation
            } else {
                "... " + conversation.substring(conversation.length - totalLen)
            }
        }
        return getHighlightedText(trimmedText, keywords, color)
    }

    fun getHighlightedText(
        stringToBeMatched: String,
        keywordsMatched: List<String>,
        color: Int
    ): SpannableStringBuilder {
        val str = SpannableStringBuilder(stringToBeMatched)
        keywordsMatched.forEach { keyword ->
            if (str.startsWith(keyword, ignoreCase = true)) {
                highlightMatchedText(str, color, 0, keyword.length)
            }
            if (str.contains(" $keyword", ignoreCase = true)) {
                var lastIndex = 0
                while (lastIndex != -1) {
                    lastIndex = str.indexOf(" $keyword", lastIndex, ignoreCase = true)
                    if (lastIndex != -1) {
                        highlightMatchedText(
                            str,
                            color,
                            lastIndex,
                            lastIndex + keyword.length + 1
                        )
                        lastIndex += " $keyword".length
                    }
                }
            }
        }
        return str
    }

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

    /**
     * isMessage is used to highlight keyword in message type search
     * for non message highlights there need to be at least two keywords present
     */
    fun findMatchedKeywords(
        keywordSearched: String?,
        str: String?,
        isMessage: Boolean = false
    ): MutableList<String> {
        val listOfKeywords = keywordSearched?.split(" ")?.map { it.trim() }
        val matchedKeywords = mutableListOf<String>()
        if (!listOfKeywords.isNullOrEmpty() && !str.isNullOrEmpty()) {
            if (listOfKeywords.size > 1 && !isMessage) {
                listOfKeywords.forEach { keyword ->
                    if (str.lowercase()
                            .contains(" ${keyword.lowercase()}")
                        || str.lowercase().startsWith(
                            keyword.lowercase()
                        )
                    ) {
                        matchedKeywords.add(keyword)
                    }
                }
            } else if (isMessage) {
                listOfKeywords.forEach { keyword ->
                    if (str.lowercase()
                            .contains(" ${keyword.lowercase()}")
                        || str.lowercase().startsWith(
                            keyword.lowercase()
                        )
                    ) {
                        matchedKeywords.add(keyword)
                    }
                }
            }
        }
        return matchedKeywords
    }
}