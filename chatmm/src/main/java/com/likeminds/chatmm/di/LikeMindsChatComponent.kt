package com.likeminds.chatmm.di

import android.app.Application
import com.likeminds.chatmm.SDKApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface LikeMindsChatComponent {
    fun inject(sdkApplication: SDKApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LikeMindsChatComponent
    }
}