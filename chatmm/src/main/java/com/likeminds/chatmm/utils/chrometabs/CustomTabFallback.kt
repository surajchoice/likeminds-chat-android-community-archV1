package com.likeminds.chatmm.utils.chrometabs

import android.content.Context
import android.net.Uri

internal fun interface CustomTabFallback {
    fun openUri(context: Context, uri: Uri)
}