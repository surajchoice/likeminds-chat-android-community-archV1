package com.likeminds.chatmm.di.dm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.dm.viewmodel.DMFeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class DMViewModelModule {

    @Binds
    abstract fun bindDMFeedViewModelFactory(factory: DMViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @DMViewModelKey(DMFeedViewModel::class)
    abstract fun bindDMFeedViewModel(dmFeedViewModel: DMFeedViewModel): ViewModel
}