package com.likeminds.chatmm.di.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.chatroom.explore.viewmodel.ExploreViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ExploreViewModelModule {

    @Binds
    abstract fun bindExploreViewModelFactory(factory: ExploreViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ExploreViewModelKey(ExploreViewModel::class)
    abstract fun bindExploreViewModel(exploreViewModel: ExploreViewModel): ViewModel
}