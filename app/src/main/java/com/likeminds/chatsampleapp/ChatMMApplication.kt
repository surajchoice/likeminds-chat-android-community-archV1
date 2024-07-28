package com.likeminds.chatsampleapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.likeminds.chatmm.LMUICallback
import com.likeminds.chatmm.LikeMindsChatUI
import com.likeminds.chatmm.theme.model.LMFonts
import com.likeminds.chatmm.theme.model.LMChatTheme
import com.likeminds.chatmm.utils.observer.ChatEvent
import com.likeminds.chatmm.widget.model.WidgetViewData
import com.likeminds.chatsampleapp.auth.util.AuthPreferences
import org.json.JSONObject

class ChatMMApplication : Application(), LMUICallback {

    private lateinit var authPreferences: AuthPreferences

    companion object {
        const val EXTRA_LOGIN = "extra of login"
    }

    override fun onCreate() {
        super.onCreate()

        authPreferences = AuthPreferences(this)

        val chatTheme = LMChatTheme.Builder()
            .headerColor(authPreferences.getHeaderColor())
            .buttonsColor(authPreferences.getButtonColor())
            .textLinkColor(authPreferences.getTextLinkColor())
            .fonts(
                LMFonts.Builder()
                    .bold("fonts/montserrat-bold.ttf")
                    .medium("fonts/montserrat-medium.ttf")
                    .regular("fonts/montserrat-regular.ttf")
                    .build()
            )
            .build()

        LikeMindsChatUI.initiateChatUI(
            this,
            this,
            chatTheme
        )
    }

    override fun login() {
        super.login()
        // override this function to trigger login.
    }

    //get widget data and modify the data and return updated data in ChatEvent
    override fun getWidgetCallback(widgetData: HashMap<String?, WidgetViewData?>) {
        val updatedWidgetData = HashMap<String?, WidgetViewData?>()

        widgetData.forEach {
            val conversationId = it.key
            var widgetViewData = it.value
            val metadata = JSONObject(widgetViewData?.metadata.toString())
            metadata.put("timestamp", System.currentTimeMillis())
            widgetViewData = widgetViewData?.toBuilder()?.metadata(metadata.toString())?.build()
            updatedWidgetData[conversationId] = widgetViewData
        }
        Handler(Looper.getMainLooper()).postDelayed({
            ChatEvent.getPublisher().notify(updatedWidgetData)
        }, 3000)
    }
}