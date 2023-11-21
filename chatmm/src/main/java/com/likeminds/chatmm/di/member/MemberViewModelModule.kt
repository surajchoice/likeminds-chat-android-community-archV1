package com.likeminds.chatmm.di.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.di.dm.DMViewModelKey
import com.likeminds.chatmm.member.viewmodel.DMAllMemberViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MemberViewModelModule {

    @Binds
    abstract fun bindDMAllMemberViewModelFactory(factory: MemberViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @DMViewModelKey(DMAllMemberViewModel::class)
    abstract fun bindDMAllMemberViewModel(dmAllMemberViewModel: DMAllMemberViewModel): ViewModel
}