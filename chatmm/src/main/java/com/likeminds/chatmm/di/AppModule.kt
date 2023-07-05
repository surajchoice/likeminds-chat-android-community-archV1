package com.likeminds.chatmm.di

import android.app.Application
import android.content.Context
import com.likeminds.chatmm.utils.mediauploader.di.AWSModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AWSModule::class])
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }
}