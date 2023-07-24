package com.likeminds.chatmm.di.search

import com.likeminds.chatmm.search.view.SearchFragment
import dagger.Subcomponent

@Subcomponent(modules = [SearchViewModelModule::class])
interface SearchComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(searchFragment: SearchFragment)
}