package com.likeminds.chatmm.di.polls

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.polls.viewmodel.CreatePollViewModel
import com.likeminds.chatmm.polls.viewmodel.PollResultViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PollsViewModelModule {

    @Binds
    abstract fun bindPollViewModelFactory(factory: PollsViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @PollsViewModelKey(CreatePollViewModel::class)
    abstract fun bindCreatePollViewModel(createPollViewModel: CreatePollViewModel): ViewModel

    @Binds
    @IntoMap
    @PollsViewModelKey(PollResultViewModel::class)
    abstract fun bindPollResultViewModel(pollResultViewModel: PollResultViewModel): ViewModel
}