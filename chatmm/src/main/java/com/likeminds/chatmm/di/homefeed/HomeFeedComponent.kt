package com.likeminds.chatmm.di.homefeed

import com.likeminds.chatmm.homefeed.view.HomeFeedFragment
import dagger.Subcomponent

@Subcomponent(modules = [HomeFeedViewModelModule::class])
interface HomeFeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeFeedComponent
    }

    fun inject(homeFeedFragment: HomeFeedFragment)
}