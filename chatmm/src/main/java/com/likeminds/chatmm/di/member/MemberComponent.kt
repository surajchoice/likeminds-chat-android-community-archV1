package com.likeminds.chatmm.di.member

import com.likeminds.chatmm.member.view.DMAllMemberActivity
import com.likeminds.chatmm.member.view.DMAllMemberFragment
import dagger.Subcomponent

@Subcomponent(modules = [MemberViewModelModule::class])
interface MemberComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MemberComponent
    }

    fun inject(dmAllMemberActivity: DMAllMemberActivity)
    fun inject(dmAllMemberFragment: DMAllMemberFragment)
}