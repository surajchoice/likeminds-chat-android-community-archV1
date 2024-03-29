package com.likeminds.chatmm

import com.likeminds.chatmm.member.model.MemberViewData

interface LMUICallback {
    fun login() {
        // to implement whenever refresh token is expired
    }

    fun openProfile(user: MemberViewData) {
        //implement to open your profile page with member data
    }
}