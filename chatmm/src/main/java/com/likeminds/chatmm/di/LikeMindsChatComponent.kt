package com.likeminds.chatmm.di

import android.app.Application
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.di.homefeed.HomeFeedComponent
import com.likeminds.chatmm.di.homefeed.HomeFeedComponentModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        HomeFeedComponentModule::class
    ]
)
interface LikeMindsChatComponent {
    fun inject(sdkApplication: SDKApplication)
    fun homeFeedComponent(): HomeFeedComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LikeMindsChatComponent
    }
}