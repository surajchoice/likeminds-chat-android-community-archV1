package com.likeminds.chatmm.utils.membertagging.util

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.likeminds.chatmm.utils.membertagging.MemberTaggingDecoder

class MemberTaggingClickableSpan(
    val color: Int,
    val regex: String,
    val underLineText: Boolean = false,
    val memberTaggingClickableSpanListener: MemberTaggingClickableSpanListener? = null
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        try {
            ds.color = color
            ds.isUnderlineText = underLineText
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(widget: View) {
        memberTaggingClickableSpanListener?.onClick(regex)
    }

    fun getMemberId(): String? {
        return MemberTaggingDecoder.getMemberIdFromRegex(regex)
    }
}

fun interface MemberTaggingClickableSpanListener {
    fun onClick(regex: String)
}