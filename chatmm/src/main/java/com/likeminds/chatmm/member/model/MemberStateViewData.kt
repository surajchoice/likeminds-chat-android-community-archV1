package com.likeminds.chatmm.member.model

import com.likeminds.chatmm.chatroom.detail.model.ManagementRightPermissionViewData

class MemberStateViewData private constructor(
    val state: Int,
    val memberViewData: MemberViewData?,
    val managerRights: List<ManagementRightPermissionViewData>?,
    val memberRights: List<ManagementRightPermissionViewData>?
) {
    class Builder {
        private var state: Int = 0
        private var memberViewData: MemberViewData? = null
        private var managerRights: List<ManagementRightPermissionViewData>? = null
        private var memberRights: List<ManagementRightPermissionViewData>? = null

        fun state(state: Int) = apply { this.state = state }
        fun memberViewData(memberViewData: MemberViewData?) =
            apply { this.memberViewData = memberViewData }

        fun managerRights(managerRights: List<ManagementRightPermissionViewData>?) =
            apply { this.managerRights = managerRights }

        fun memberRights(memberRights: List<ManagementRightPermissionViewData>?) =
            apply { this.memberRights = memberRights }

        fun build() = MemberStateViewData(
            state,
            memberViewData,
            managerRights,
            memberRights
        )
    }

    fun toBuilder(): Builder {
        return Builder().state(state)
            .memberViewData(memberViewData)
            .managerRights(managerRights)
            .memberRights(memberRights)
    }
}