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
import com.likeminds.chatmm.di.dm.DMComponent
import com.likeminds.chatmm.di.explore.ExploreComponent
import com.likeminds.chatmm.di.homefeed.HomeFeedComponent
import com.likeminds.chatmm.di.media.MediaComponent
import com.likeminds.chatmm.di.member.MemberComponent
import com.likeminds.chatmm.di.polls.PollsComponent
import com.likeminds.chatmm.di.reactions.ReactionsComponent
import com.likeminds.chatmm.di.report.ReportComponent
import com.likeminds.chatmm.di.search.SearchComponent
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
    private var searchComponent: SearchComponent? = null
    private var pollsComponent: PollsComponent? = null
    private var reactionsComponent: ReactionsComponent? = null
    private var reportComponent: ReportComponent? = null
    private var dmComponent: DMComponent? = null
    private var memberComponent: MemberComponent? = null

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

    /**
     * initiate and return PollsComponent: All dependencies required for polls package
     * */
    fun pollsComponent(): PollsComponent? {
        if (pollsComponent == null) {
            pollsComponent = likeMindsChatComponent?.pollsComponent()?.create()
        }
        return pollsComponent
    }

    /**
     * initiate and return [SearchComponent]: All dependencies required for search screens
     */
    fun searchComponent(): SearchComponent? {
        if (searchComponent == null) {
            searchComponent = likeMindsChatComponent?.searchComponent()?.create()
        }

        return searchComponent
    }

    /**
     * initiate and return ReactionsComponent: All dependencies required for reactions package
     * */
    fun reactionsComponent(): ReactionsComponent? {
        if (reactionsComponent == null) {
            reactionsComponent = likeMindsChatComponent?.reactionsComponent()?.create()
        }
        return reactionsComponent
    }

    /**
     * initiate and return ReportComponent: All dependencies required for report package
     * */
    fun reportComponent(): ReportComponent? {
        if (reportComponent == null) {
            reportComponent = likeMindsChatComponent?.reportComponent()?.create()
        }
        return reportComponent
    }

    fun dmComponent(): DMComponent? {
        if(dmComponent == null) {
            dmComponent = likeMindsChatComponent?.dmComponent()?.create()
        }
        return dmComponent
    }

    fun memberComponent(): MemberComponent? {
        if(memberComponent == null) {
            memberComponent = likeMindsChatComponent?.memberComponent()?.create()
        }
        return memberComponent
    }
}