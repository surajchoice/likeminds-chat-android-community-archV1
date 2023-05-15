package com.likeminds.chatmm

import android.app.Application
import android.content.Context
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.likeminds.chatmm.di.DaggerLikeMindsChatComponent
import com.likeminds.chatmm.di.LikeMindsChatComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SDKApplication {
    @Inject
    lateinit var transferUtility: TransferUtility

    private var likeMindsChatComponent: LikeMindsChatComponent? = null

    companion object {
        const val LOG_TAG = "LikeMinds"
        private var sdkApplicationInstance: SDKApplication? = null

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
    }

    fun initSDKApplication(
        application: Application
    ) {
        // todo: branding and domain
        initAppComponent(application)
        initAWSMobileClient(application)
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
}