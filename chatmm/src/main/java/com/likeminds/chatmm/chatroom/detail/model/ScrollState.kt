package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val SCROLL_DOWN = -1
const val SCROLL_UP = 0
const val SCROLL_LEFT = 1
const val SCROLL_RIGHT = 2

@IntDef(SCROLL_DOWN, SCROLL_UP, SCROLL_LEFT, SCROLL_RIGHT)
@Retention(AnnotationRetention.SOURCE)
internal annotation class ScrollState