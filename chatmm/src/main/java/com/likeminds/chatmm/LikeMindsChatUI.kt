package com.likeminds.chatmm

import android.app.Application
import android.util.Log
import com.likeminds.chatmm.branding.model.SetBrandingRequest

object LikeMindsChatUI {
    /**
     * Call this function to configure SDK in client's app
     *
     * @param application: application instance of client's app
     * @param brandingRequest: branding request from client
     **/
    fun initLikeChatFeedUI(
        application: Application,
        brandingRequest: SetBrandingRequest
    ) {
        Log.d(SDKApplication.LOG_TAG, "initiate LikeMindsChatUI called")

        //create object of SDKApplication
        val sdk = SDKApplication.getInstance()

        //call initSDKApplication to initialise sdk
        sdk.initSDKApplication(
            application,
            brandingRequest
        )
    }
}