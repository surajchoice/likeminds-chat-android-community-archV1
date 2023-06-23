package com.likeminds.chatmm.utils.file.model

import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

data class LocalAppData(
    val appId: Int,
    val appName: String,
    val appIcon: Drawable,
    val resolveInfo: ResolveInfo
)