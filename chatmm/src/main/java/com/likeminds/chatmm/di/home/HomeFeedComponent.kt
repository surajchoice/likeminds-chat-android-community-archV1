package com.likeminds.chatmm.di.home

import com.likeminds.chatmm.home.view.HomeFeedFragment
import dagger.Subcomponent

@Subcomponent(modules = [HomeFeedViewModelModule::class])
interface HomeFeedComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeFeedComponent
    }

    fun inject(homeFeedFragment: HomeFeedFragment)
}