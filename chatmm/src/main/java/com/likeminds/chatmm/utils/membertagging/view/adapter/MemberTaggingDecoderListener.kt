package com.likeminds.chatmm.utils.membertagging.view.adapter

import android.net.Uri

fun interface MemberTaggingDecoderListener {
    fun onTagClick(tag: Uri)
}