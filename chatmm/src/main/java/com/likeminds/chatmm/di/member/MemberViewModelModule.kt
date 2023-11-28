package com.likeminds.chatmm.di.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.likeminds.chatmm.di.dm.DMViewModelKey
import com.likeminds.chatmm.member.viewmodel.CommunityMembersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MemberViewModelModule {

    @Binds
    abstract fun bindMemberViewModelFactory(factory: MemberViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @DMViewModelKey(CommunityMembersViewModel::class)
    abstract fun bindCommunityMembersViewModel(communityMembersViewModel: CommunityMembersViewModel): ViewModel
}