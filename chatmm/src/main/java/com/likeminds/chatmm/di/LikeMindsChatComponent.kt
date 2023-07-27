package com.likeminds.chatmm.di

import android.app.Application
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.di.chatroomdetail.ChatroomDetailComponent
import com.likeminds.chatmm.di.chatroomdetail.ChatroomDetailComponentModule
import com.likeminds.chatmm.di.explore.ExploreComponent
import com.likeminds.chatmm.di.explore.ExploreComponentModule
import com.likeminds.chatmm.di.homefeed.HomeFeedComponent
import com.likeminds.chatmm.di.homefeed.HomeFeedComponentModule
import com.likeminds.chatmm.di.media.MediaComponent
import com.likeminds.chatmm.di.media.MediaComponentModule
import com.likeminds.chatmm.di.polls.PollsComponent
import com.likeminds.chatmm.di.polls.PollsComponentModule
import com.likeminds.chatmm.di.reactions.ReactionsComponent
import com.likeminds.chatmm.di.reactions.ReactionsComponentModule
import com.likeminds.chatmm.di.search.SearchComponent
import com.likeminds.chatmm.pushnotification.util.LMChatNotificationHandler
import com.likeminds.chatmm.pushnotification.util.NotificationActionBroadcastReceiver
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        HomeFeedComponentModule::class,
        ExploreComponentModule::class,
        ChatroomDetailComponentModule::class,
        MediaComponentModule::class,
        PollsComponentModule::class,
        ReactionsComponentModule::class
    ]
)
interface LikeMindsChatComponent {
    fun inject(sdkApplication: SDKApplication)
    fun inject(lmNotificationHandler: LMChatNotificationHandler)
    fun inject(broadcastReceiver: NotificationActionBroadcastReceiver)
    fun homeFeedComponent(): HomeFeedComponent.Factory
    fun exploreComponent(): ExploreComponent.Factory
    fun chatroomDetailComponent(): ChatroomDetailComponent.Factory
    fun mediaComponent(): MediaComponent.Factory
    fun pollsComponent(): PollsComponent.Factory
    fun searchComponent(): SearchComponent.Factory
    fun reactionsComponent(): ReactionsComponent.Factory

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): LikeMindsChatComponent
    }
}