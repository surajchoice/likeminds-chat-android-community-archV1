package com.likeminds.chatmm.media.model

import androidx.annotation.StringDef

const val SORT_BY_NAME = "name"
const val SORT_BY_DATE = "date"

@StringDef(
    SORT_BY_NAME, SORT_BY_DATE
)

@Retention(AnnotationRetention.SOURCE)
annotation class SortType