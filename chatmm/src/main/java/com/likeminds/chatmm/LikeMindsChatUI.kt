package com.likeminds.chatmm

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.chatmm.branding.model.SetBrandingRequest
import com.likeminds.chatmm.homefeed.model.GroupChatResponse
import com.likeminds.chatmm.homefeed.model.HomeFeedExtras
import com.likeminds.chatmm.homefeed.view.HomeFeedFragment

object LikeMindsChatUI {
    /**
     * Call this function to configure SDK in client's app
     *
     * @param application: application instance of client's app
     * @param brandingRequest: branding request from client
     **/
    fun initiateGroupChatUI(
        application: Application,
        lmUICallback: LMUICallback,
        brandingRequest: SetBrandingRequest
    ) {
        Log.d(SDKApplication.LOG_TAG, "initiate LikeMindsChatUI called")

        //create object of SDKApplication
        val sdk = SDKApplication.getInstance()

        //call initSDKApplication to initialise sdk
        sdk.initSDKApplication(
            application,
            lmUICallback,
            brandingRequest
        )
    }

    fun initiateHomeFeed(
        activity: AppCompatActivity,
        containerViewId: Int,
        apiKey: String,
        userName: String,
        userId: String? = null,
        isGuest: Boolean? = false,
        cb: (response: GroupChatResponse?) -> Unit
    ) {
        Log.d(SDKApplication.LOG_TAG, "initiate group chat called")
        Log.d(
            SDKApplication.LOG_TAG, """
            container id: $containerViewId
            user_name: $userName
            user id: $userId
            isGuest: $isGuest
        """.trimIndent()
        )

        val extra = HomeFeedExtras.Builder()
            .userName(userName)
            .userId(userId)
            .isGuest(isGuest)
            .apiKey(apiKey)
            .build()

        val fragment = HomeFeedFragment.getInstance(extra) { response ->
            cb(response)
        }

        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(containerViewId, fragment, containerViewId.toString())
        transaction.setReorderingAllowed(true)
        Log.d(SDKApplication.LOG_TAG, "showing group chat")
        transaction.commitNowAllowingStateLoss()
    }

    fun setBranding(brandingRequest: SetBrandingRequest) {
        val sdk = SDKApplication.getInstance()
        sdk.setupBranding(brandingRequest)
    }
}