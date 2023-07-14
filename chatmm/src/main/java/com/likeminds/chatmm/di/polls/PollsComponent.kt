package com.likeminds.chatmm.di.polls

import com.likeminds.chatmm.polls.view.*
import dagger.Subcomponent

@Subcomponent(modules = [PollsViewModelModule::class])
interface PollsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): PollsComponent
    }

    fun inject(addPollOptionDialog: AddPollOptionDialog)
    fun inject(createConversationPollDialog: CreateConversationPollDialog)
    fun inject(pollResultFragment: PollResultFragment)
    fun inject(pollResultsActivity: PollResultsActivity)
    fun inject(pollResultTabFragment: PollResultTabFragment)
}