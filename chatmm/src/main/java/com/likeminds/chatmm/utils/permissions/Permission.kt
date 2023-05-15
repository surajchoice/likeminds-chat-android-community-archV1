package com.likeminds.chatmm.utils.permissions

import android.Manifest
import androidx.annotation.DrawableRes
import com.likeminds.chatmm.R

class Permission private constructor(
    val permissionName: String,
    val requestCode: Int,
    val preDialogMessage: String,
    val deniedDialogMessage: String,
    @param:DrawableRes @field:DrawableRes
    val dialogImage: Int
) {
    companion object {

        private const val LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        private const val CAMERA = Manifest.permission.CAMERA
        private const val REQUEST_LOCATION = 10101
        private const val REQUEST_STORAGE = 10102
        private const val REQUEST_RECORD_AUDIO = 10103
        private const val REQUEST_CAMERA = 10104

        fun getCameraPermissionData(): Permission {
            return Permission(
                CAMERA,
                REQUEST_CAMERA,
                "The app allows users to click pictures from camera and share it with other users.",
                "To share pictures, allow LikeMinds access to camera. Tap on Settings > Permission, and turn Camera on.",
                R.drawable.ic_camera_white
            )
        }

        fun getStoragePermissionData(): Permission {
            return Permission(
                WRITE_STORAGE,
                REQUEST_STORAGE,
                "To easily receive and send photos, videos and other files, allow LikeMinds access to your device’s photos, media and files.",
                "To send media, allow LikeMinds access to your device’s photos, media and files. Tap on Settings > Permission, and turn Storage on.",
                R.drawable.ic_folder
            )
        }
    }
}
