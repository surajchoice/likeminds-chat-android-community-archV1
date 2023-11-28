package com.likeminds.chatmm.di.member

import com.likeminds.chatmm.member.view.CommunityMembersActivity
import com.likeminds.chatmm.member.view.CommunityMembersFragment
import dagger.Subcomponent

@Subcomponent(modules = [MemberViewModelModule::class])
interface MemberComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MemberComponent
    }

    fun inject(communityMembersActivity: CommunityMembersActivity)
    fun inject(communityMembersFragment: CommunityMembersFragment)
}