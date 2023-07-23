package com.likeminds.chatmm.utils.link

fun interface OnLinkClickListener {
    fun onLinkClicked(url: String): Boolean
}