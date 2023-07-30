package com.likeminds.chatmm.report.model

import androidx.annotation.IntDef

const val REPORT_TYPE_CHATROOM = 0
const val REPORT_TYPE_MEMBER = 1
const val REPORT_TYPE_COMMUNITY = 2
const val REPORT_TYPE_CONVERSATION = 3
const val REPORT_TYPE_LINK = 4

@IntDef(
    REPORT_TYPE_MEMBER,
    REPORT_TYPE_CHATROOM,
    REPORT_TYPE_CONVERSATION,
    REPORT_TYPE_COMMUNITY,
    REPORT_TYPE_LINK
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType