package com.likeminds.chatmm.utils.permissions

import android.os.Build
import androidx.annotation.NonNull
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class PermissionManager {

    companion object {
        const val REQUEST_CODE_SETTINGS_PERMISSION = 100

        fun performTaskWithPermission(
            @NonNull activity: BaseAppCompatActivity,
            @NonNull task: PermissionTask,
            permission: Permission,
            showInitialPopup: Boolean,
            showDeniedPopup: Boolean,
            setInitialPopupDismissible: Boolean = false,
            setDeniedPopupDismissible: Boolean = false,
            permissionDeniedCallback: PermissionDeniedCallback? = null,
        ) {
            if (activity.hasPermission(permission))
                task.doTask()
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity.canRequestPermission(permission)) {
                        if (showInitialPopup) {
                            val permissionDialog = PermissionDialog(
                                activity,
                                task,
                                permission,
                                PermissionDialog.Mode.INIT,
                                permissionDeniedCallback
                            )
                            permissionDialog.setCanceledOnTouchOutside(setInitialPopupDismissible)
                            permissionDialog.show()
                        } else {
                            activity.requestPermission(permission, object : PermissionCallback {
                                override fun onGrant() {
                                    task.doTask()
                                }

                                override fun onDeny() {
                                    if (showDeniedPopup) {
                                        val permissionDialog = PermissionDialog(
                                            activity,
                                            task,
                                            permission,
                                            PermissionDialog.Mode.DENIED,
                                            permissionDeniedCallback
                                        )
                                        permissionDialog.setCanceledOnTouchOutside(
                                            setDeniedPopupDismissible
                                        )
                                        permissionDialog.setCancelable(setDeniedPopupDismissible)
                                        permissionDialog.show()
                                    } else {
                                        permissionDeniedCallback?.onDeny()
                                    }
                                }
                            })
                        }
                    } else {
                        if (showDeniedPopup) {
                            val permissionDialog = PermissionDialog(
                                activity,
                                task,
                                permission,
                                PermissionDialog.Mode.DENIED,
                                permissionDeniedCallback
                            )
                            permissionDialog.setCanceledOnTouchOutside(setDeniedPopupDismissible)
                            permissionDialog.setCancelable(setDeniedPopupDismissible)
                            permissionDialog.show()
                        } else {
                            permissionDeniedCallback?.onDeny()
                        }
                    }
                }
            }
        }
    }
}
