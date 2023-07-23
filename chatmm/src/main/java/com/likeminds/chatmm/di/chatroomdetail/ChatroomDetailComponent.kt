package com.likeminds.chatmm.di.chatroomdetail

import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import dagger.Subcomponent

@Subcomponent(modules = [ChatroomDetailViewModelModule::class])
interface ChatroomDetailComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatroomDetailComponent
    }

    fun inject(chatroomDetailFragment: ChatroomDetailFragment)
}