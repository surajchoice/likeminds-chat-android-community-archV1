package com.likeminds.chatmm.di.explore

import com.likeminds.chatmm.chatroom.explore.view.ChatroomExploreActivity
import com.likeminds.chatmm.chatroom.explore.view.ChatroomExploreFragment
import dagger.Subcomponent

@Subcomponent(modules = [ExploreViewModelModule::class])
interface ExploreComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ExploreComponent
    }

    fun inject(chatroomExploreActivity: ChatroomExploreActivity)
    fun inject(chatroomExploreFragment: ChatroomExploreFragment)
}