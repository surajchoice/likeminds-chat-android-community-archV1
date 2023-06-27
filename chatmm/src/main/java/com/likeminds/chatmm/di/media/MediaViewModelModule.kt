package com.likeminds.chatmm.di.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MediaViewModelModule {

    @Binds
    abstract fun bindMediaViewModelFactory(factory: MediaViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MediaViewModelKey(MediaViewModel::class)
    abstract fun bindMediaViewModel(mediaViewModel: MediaViewModel): ViewModel
}