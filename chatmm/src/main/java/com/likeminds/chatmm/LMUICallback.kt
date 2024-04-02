package com.likeminds.chatmm

import com.likeminds.chatmm.widget.model.WidgetViewData
import org.json.JSONObject

interface LMUICallback {
    fun login() {
        // to implement whenever refresh token is expired
    }

    fun getWidgetCallback(widgetData: HashMap<String?, WidgetViewData?>) {

    }

    fun getTransactionData(): HashMap<String, JSONObject?> {
        return HashMap()
    }
}