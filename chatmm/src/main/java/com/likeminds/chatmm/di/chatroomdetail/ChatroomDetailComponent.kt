package com.likeminds.chatmm.di.chatroomdetail

import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import com.likeminds.chatmm.chatroom.detail.view.ViewParticipantsFragment
import dagger.Subcomponent

@Subcomponent(modules = [ChatroomDetailViewModelModule::class])
interface ChatroomDetailComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatroomDetailComponent
    }

    fun inject(chatroomDetailFragment: ChatroomDetailFragment)
    fun inject(viewParticipantsFragment: ViewParticipantsFragment)
}