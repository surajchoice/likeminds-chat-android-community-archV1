package com.likeminds.chatmm.di.reactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.reactions.viewmodel.ReactionsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ReactionsViewModelModule {

    @Binds
    abstract fun bindReactionsViewModelFactory(factory: ReactionsViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ReactionsViewModelKey(ReactionsViewModel::class)
    abstract fun bindReactionsViewModel(reactionsViewModel: ReactionsViewModel): ViewModel
}