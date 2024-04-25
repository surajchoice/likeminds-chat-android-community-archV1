package com.likeminds.chatmm.di.member

import com.likeminds.chatmm.member.view.LMChatCommunityMembersActivity
import com.likeminds.chatmm.member.view.LMChatCommunityMembersFragment
import dagger.Subcomponent

@Subcomponent(modules = [MemberViewModelModule::class])
interface MemberComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MemberComponent
    }

    fun inject(communityMembersActivity: LMChatCommunityMembersActivity)
    fun inject(communityMembersFragment: LMChatCommunityMembersFragment)
}