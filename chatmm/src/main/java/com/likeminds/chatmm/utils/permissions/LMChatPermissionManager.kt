package com.likeminds.chatmm.utils.permissions

import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.permissions.model.LMChatPermissionExtras
import com.likeminds.chatmm.utils.permissions.view.LMChatPermissionDialog

class LMChatPermissionManager {

    companion object {
        fun performTaskWithPermissionExtras(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: LMChatPermissionTask,
            permissionExtras: LMChatPermissionExtras,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            lmChatPermissionDeniedCallback: LMChatPermissionDeniedCallback? = null,
        ) {
            val permissions = permissionExtras.permissions
            if (activity.hasPermissions(permissions)) {
                task.doTask()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermissions(permissions)) {
                        if (showInitialPopup) {
                            val lmChatPermissionDialog = LMChatPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                null,
                                LMChatPermissionDialog.Mode.INIT,
                                lmChatPermissionDeniedCallback,
                                permissionExtras
                            )
                            lmChatPermissionDialog.setCanceledOnTouchOutside(
                                setInitialPopupDismissible
                            )
                            lmChatPermissionDialog.show()
                        } else {
                            activity.requestMultiplePermissions(
                                permissionExtras,
                                object : LMChatPermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        if (showDeniedPopup) {
                                            val lmChatPermissionDialog = LMChatPermissionDialog(
                                                activity,
                                                settingsPermissionLauncher,
                                                task,
                                                null,
                                                LMChatPermissionDialog.Mode.DENIED,
                                                lmChatPermissionDeniedCallback,
                                                permissionExtras
                                            )
                                            lmChatPermissionDialog.setCanceledOnTouchOutside(
                                                setDeniedPopupDismissible
                                            )
                                            lmChatPermissionDialog.setCancelable(
                                                setDeniedPopupDismissible
                                            )
                                            lmChatPermissionDialog.show()
                                        } else {
                                            lmChatPermissionDeniedCallback?.onDeny()
                                        }
                                    }
                                })
                        }
                    } else {
                        if (showDeniedPopup) {
                            val lmChatPermissionDialog = LMChatPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                null,
                                LMChatPermissionDialog.Mode.DENIED,
                                lmChatPermissionDeniedCallback,
                                permissionExtras
                            )
                            lmChatPermissionDialog.setCanceledOnTouchOutside(
                                setDeniedPopupDismissible
                            )
                            lmChatPermissionDialog.setCancelable(setDeniedPopupDismissible)
                            lmChatPermissionDialog.show()
                        } else {
                            lmChatPermissionDeniedCallback?.onDeny()
                        }
                    }
                }
            }
        }

        fun performTaskWithPermission(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            task: LMChatPermissionTask,
            lmChatPermission: LMChatPermission,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            lmChatPermissionDeniedCallback: LMChatPermissionDeniedCallback? = null,
        ) {
            if (activity.hasPermission(lmChatPermission))
                task.doTask()
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermission(lmChatPermission)) {
                        if (showInitialPopup) {
                            val lmChatPermissionDialog = LMChatPermissionDialog(
                                activity,
                                settingsPermissionLauncher,
                                task,
                                lmChatPermission,
                                LMChatPermissionDialog.Mode.INIT,
                                lmChatPermissionDeniedCallback
                            )
                            lmChatPermissionDialog.setCanceledOnTouchOutside(
                                setInitialPopupDismissible
                            )
                            lmChatPermissionDialog.show()
                        } else {
                            activity.requestPermission(
                                lmChatPermission,
                                object : LMChatPermissionCallback {
                                    override fun onGrant() {
                                        task.doTask()
                                    }

                                    override fun onDeny() {
                                        showDeniedDialog(
                                            activity,
                                            settingsPermissionLauncher,
                                            showDeniedPopup,
                                            task,
                                            lmChatPermission,
                                            setDeniedPopupDismissible,
                                            lmChatPermissionDeniedCallback
                                        )
                                    }
                                })
                        }
                    } else {
                        showDeniedDialog(
                            activity,
                            settingsPermissionLauncher,
                            showDeniedPopup,
                            task,
                            lmChatPermission,
                            setDeniedPopupDismissible,
                            lmChatPermissionDeniedCallback
                        )
                    }
                }
            }
        }

        private fun showDeniedDialog(
            activity: BaseAppCompatActivity,
            settingsPermissionLauncher: ActivityResultLauncher<Intent>,
            showDeniedPopup: Boolean,
            task: LMChatPermissionTask,
            lmChatPermission: LMChatPermission,
            setDeniedPopupDismissible: Boolean,
            lmChatPermissionDeniedCallback: LMChatPermissionDeniedCallback?,
        ) {
            if (showDeniedPopup) {
                val lmChatPermissionDialog = LMChatPermissionDialog(
                    activity,
                    settingsPermissionLauncher,
                    task,
                    lmChatPermission,
                    LMChatPermissionDialog.Mode.DENIED,
                    lmChatPermissionDeniedCallback
                )
                lmChatPermissionDialog.setCanceledOnTouchOutside(setDeniedPopupDismissible)
                lmChatPermissionDialog.setCancelable(setDeniedPopupDismissible)
                lmChatPermissionDialog.show()
            } else {
                lmChatPermissionDeniedCallback?.onDeny()
            }
        }
    }
}
