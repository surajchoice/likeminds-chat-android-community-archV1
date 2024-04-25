package com.likeminds.chatmm

import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.widget.model.WidgetViewData

interface LMUICallback {
    fun login() {
        // to implement whenever refresh token is expired
    }

    fun openProfile(user: MemberViewData) {
        //implement to open your profile page with member data
    }

    fun getWidgetCallback(widgetData: HashMap<String?, WidgetViewData?>) {
        //implement to get widget data in conversation id
    }
}