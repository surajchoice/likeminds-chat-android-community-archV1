package com.likeminds.chatmm

import android.app.Application
import android.content.Context
import com.amazonaws.mobile.client.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.branding.model.SetBrandingRequest
import com.likeminds.chatmm.di.DaggerLikeMindsChatComponent
import com.likeminds.chatmm.di.LikeMindsChatComponent
import com.likeminds.chatmm.di.chatroomdetail.ChatroomDetailComponent
import com.likeminds.chatmm.di.explore.ExploreComponent
import com.likeminds.chatmm.di.homefeed.HomeFeedComponent
import com.likeminds.chatmm.di.media.MediaComponent
import com.likeminds.chatmm.di.polls.PollsComponent
import com.likeminds.likemindschat.LMChatClient
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SDKApplication {

    @Inject
    lateinit var transferUtility: TransferUtility

    private var likeMindsChatComponent: LikeMindsChatComponent? = null

    private var homeFeedComponent: HomeFeedComponent? = null
    private var exploreComponent: ExploreComponent? = null
    private var chatroomDetailComponent: ChatroomDetailComponent? = null
    private var mediaComponent: MediaComponent? = null
    private var pollsComponent: PollsComponent? = null

    companion object {
        const val LOG_TAG = "LikeMindsChat"
        private var sdkApplicationInstance: SDKApplication? = null
        private var lmUICallback: LMUICallback? = null

        /**
         * @return Singleton Instance of SDK Application class, which used for injecting dagger in fragments.
         * */
        @JvmStatic
        fun getInstance(): SDKApplication {
            if (sdkApplicationInstance == null) {
                sdkApplicationInstance = SDKApplication()
            }
            return sdkApplicationInstance!!
        }

        /**
         * @return Singleton Instance of Call backs required
         * */
        @JvmStatic
        fun getLikeMindsCallback(): LMUICallback? {
            return lmUICallback
        }
    }

    fun initSDKApplication(
        application: Application,
        lmUICallback: LMUICallback,
        brandingRequest: SetBrandingRequest
    ) {
        LMChatClient.Builder(application)
            .build()
        SDKApplication.lmUICallback = lmUICallback
        setupBranding(brandingRequest)
        initAppComponent(application)
        EmojiManager.install(GoogleEmojiProvider())
        initAWSMobileClient(application)
    }

    // sets branding to the app
    fun setupBranding(setBrandingRequest: SetBrandingRequest) {
        LMBranding.setBranding(setBrandingRequest)
    }

    private fun initAWSMobileClient(applicationContext: Context) {
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails?) {
                }

                override fun onError(e: java.lang.Exception?) {
                }
            })
    }

    /**
     * initiate dagger for the sdk
     *
     * @param application : The client will pass instance application to the function
     * */
    private fun initAppComponent(application: Application) {
        if (likeMindsChatComponent == null) {
            likeMindsChatComponent = DaggerLikeMindsChatComponent.builder()
                .application(application)
                .build()
        }
        likeMindsChatComponent!!.inject(this)
    }

    /**
     * initiate and return HomeFeedComponent: All dependencies required for home package
     * */
    fun homeFeedComponent(): HomeFeedComponent? {
        if (homeFeedComponent == null) {
            homeFeedComponent = likeMindsChatComponent?.homeFeedComponent()?.create()
        }
        return homeFeedComponent
    }

    /**
     * initiate and return ExploreComponent: All dependencies required for explore screen
     * */
    fun exploreComponent(): ExploreComponent? {
        if (exploreComponent == null) {
            exploreComponent = likeMindsChatComponent?.exploreComponent()?.create()
        }
        return exploreComponent
    }

    /**
     * initiate and return ChatroomDetailComponent: All dependencies required for chatroom screen package
     * */
    fun chatroomDetailComponent(): ChatroomDetailComponent? {
        if (chatroomDetailComponent == null) {
            chatroomDetailComponent = likeMindsChatComponent?.chatroomDetailComponent()?.create()
        }

        return chatroomDetailComponent
    }

    /**
     * initiate and return MediaComponent: All dependencies required for media package
     * */
    fun mediaComponent(): MediaComponent? {
        if (mediaComponent == null) {
            mediaComponent = likeMindsChatComponent?.mediaComponent()?.create()
        }
        return mediaComponent
    }

    fun pollsComponent(): PollsComponent? {
        if (pollsComponent == null) {
            pollsComponent = likeMindsChatComponent?.pollsComponent()?.create()
        }
        return pollsComponent
    }
}