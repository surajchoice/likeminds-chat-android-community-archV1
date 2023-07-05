package com.likeminds.chatsampleapp

import android.app.Application
import com.likeminds.chatmm.LMUICallback
import com.likeminds.chatmm.LikeMindsChatUI
import com.likeminds.chatmm.branding.model.LMFonts
import com.likeminds.chatmm.branding.model.SetBrandingRequest
import com.likeminds.chatsampleapp.auth.util.AuthPreferences

class ChatMMApplication : Application(), LMUICallback {

    private lateinit var authPreferences: AuthPreferences

    companion object {
        const val EXTRA_LOGIN = "extra of login"
    }

    override fun onCreate() {
        super.onCreate()

        authPreferences = AuthPreferences(this)

        val brandingRequest = SetBrandingRequest.Builder()
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

        LikeMindsChatUI.initiateGroupChatUI(
            this,
            this,
            brandingRequest
        )
    }

    override fun login() {
        super.login()
        // override this function to trigger login.
    }
}