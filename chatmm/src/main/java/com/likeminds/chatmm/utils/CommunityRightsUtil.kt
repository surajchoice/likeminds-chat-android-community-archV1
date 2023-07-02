package com.likeminds.chatmm.utils

import com.likeminds.chatmm.chatroom.detail.model.*

internal object CommunityRightsUtil {

    /**
     * Manager Rights
     */
    fun hasApproveMemberRight(managerRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasManagerRight(managerRights, MANAGER_RIGHT_APPROVE_REMOVE_MEMBERS)
    }

    fun hasDeleteChatRoomRight(managerRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasManagerRight(managerRights, MANAGER_RIGHT_DELETE_ROOMS)
    }

    fun hasEditCommunityDetailRight(managerRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasManagerRight(managerRights, MANAGER_RIGHT_EDIT_COMMUNITY)
    }

    private fun checkHasManagerRight(
        managerRights: List<ManagementRightPermissionViewData>?,
        rightState: Int,
    ): Boolean {
        var value = false
        managerRights?.singleOrNull {
            it.state == rightState
        }?.let {
            value = it.isSelected
        }
        return value
    }

    /**
     * Member Rights
     */
    fun hasCreateChatRoomRight(memberRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasMemberRight(memberRights, MEMBER_RIGHT_CREATE_ROOMS)
    }

    fun hasCreateEventChatRoomRight(memberRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasMemberRight(memberRights, MEMBER_RIGHT_CREATE_EVENT)
    }

    fun hasCreatePollChatRoomRight(memberRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasMemberRight(memberRights, MEMBER_RIGHT_CREATE_POLL)
    }

    fun hasInviteByPrivateLinkRight(memberRights: List<ManagementRightPermissionViewData>?): Boolean {
        return checkHasMemberRight(memberRights, MEMBER_RIGHT_INVITE_PRIVATE_LINK)
    }

    private fun checkHasMemberRight(
        memberRights: List<ManagementRightPermissionViewData>?,
        rightState: Int,
    ): Boolean {
        var value = false
        memberRights?.singleOrNull {
            it.state == rightState
        }?.let {
            value = it.isSelected && it.isLocked == false
        }
        return value
    }

}