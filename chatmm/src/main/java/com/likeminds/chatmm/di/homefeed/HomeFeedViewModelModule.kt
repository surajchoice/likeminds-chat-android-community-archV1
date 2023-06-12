package com.likeminds.chatmm.di.homefeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.homefeed.viewmodel.HomeFeedViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class HomeFeedViewModelModule {

    @Binds
    abstract fun bindHomeFeedViewModelFactory(factory: HomeFeedViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @HomeFeedViewModelKey(HomeFeedViewModel::class)
    abstract fun bindHomeFeedViewModel(homeFeedViewModel: HomeFeedViewModel): ViewModel
}