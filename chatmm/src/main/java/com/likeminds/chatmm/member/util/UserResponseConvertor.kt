package com.likeminds.chatmm.member.util

import com.likeminds.chatmm.member.model.UserResponse
import com.likeminds.likemindschat.user.model.InitiateUserResponse
import com.likeminds.likemindschat.user.model.ValidateUserResponse


object UserResponseConvertor {

    //convert initiate user response to user response
    fun getUserResponse(initiateUserResponse: InitiateUserResponse): UserResponse {
        return UserResponse(
            user = initiateUserResponse.user,
            community = initiateUserResponse.community,
            appAccess = initiateUserResponse.appAccess,
            accessToken = initiateUserResponse.accessToken,
            refreshToken = initiateUserResponse.refreshToken
        )
    }

    //convert validate user response to user response
    fun getUserResponse(validateUserResponse: ValidateUserResponse): UserResponse {
        return UserResponse(
            user = validateUserResponse.user,
            community = validateUserResponse.community,
            appAccess = validateUserResponse.appAccess
        )
    }
}