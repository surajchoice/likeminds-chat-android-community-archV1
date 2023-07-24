package com.likeminds.chatmm.di.reactions

import com.likeminds.chatmm.reactions.view.ReactionListFragment
import com.likeminds.chatmm.reactions.view.ReactionsListDialog
import dagger.Subcomponent

@Subcomponent(modules = [ReactionsViewModelModule::class])
interface ReactionsComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReactionsComponent
    }

    fun inject(reactionListFragment: ReactionListFragment)
    fun inject(reactionsListDialog: ReactionsListDialog)
}