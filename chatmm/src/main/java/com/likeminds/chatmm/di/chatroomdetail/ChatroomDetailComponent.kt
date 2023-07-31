package com.likeminds.chatmm.di.chatroomdetail

import com.likeminds.chatmm.chatroom.detail.view.*
import dagger.Subcomponent

@Subcomponent(modules = [ChatroomDetailViewModelModule::class])
interface ChatroomDetailComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatroomDetailComponent
    }

    fun inject(chatroomDetailFragment: ChatroomDetailFragment)
    fun inject(chatroomDetailActivity: ChatroomDetailActivity)
    fun inject(viewParticipantsActivity: ViewParticipantsActivity)
    fun inject(viewParticipantsFragment: ViewParticipantsFragment)
}