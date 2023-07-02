package com.likeminds.chatmm.chatroom.detail.model

import androidx.annotation.IntDef

const val MANAGER_RIGHT_DELETE_ROOMS = 0
const val MANAGER_RIGHT_APPROVE_REMOVE_MEMBERS = 1
const val MANAGER_RIGHT_EDIT_COMMUNITY = 2
const val MANAGER_RIGHT_VIEW_CONTACT_INFO = 3
const val MANAGER_RIGHT_ADD_MANAGERS = 4

@IntDef(
    MANAGER_RIGHT_DELETE_ROOMS,
    MANAGER_RIGHT_APPROVE_REMOVE_MEMBERS,
    MANAGER_RIGHT_EDIT_COMMUNITY,
    MANAGER_RIGHT_VIEW_CONTACT_INFO,
    MANAGER_RIGHT_ADD_MANAGERS
)
@Retention(AnnotationRetention.SOURCE)
annotation class ManagerRightsState {
    companion object {
        fun isViewContactInfo(managerRightState: Int?): Boolean {
            return managerRightState == MANAGER_RIGHT_VIEW_CONTACT_INFO
        }

        fun isAddManagers(managerRightState: Int?): Boolean {
            return managerRightState == MANAGER_RIGHT_ADD_MANAGERS
        }

        fun isDefaultRight(managerRightState: Int?): Boolean {
            return managerRightState == MANAGER_RIGHT_DELETE_ROOMS
                    || managerRightState == MANAGER_RIGHT_APPROVE_REMOVE_MEMBERS
                    || managerRightState == MANAGER_RIGHT_EDIT_COMMUNITY
        }
    }
}