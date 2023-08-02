package com.likeminds.chatmm.media.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkInfo
import com.likeminds.chatmm.R
import com.likeminds.chatmm.media.model.VIDEO
import com.likeminds.chatmm.utils.ValueUtils.getMediaType
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.downloader.DownloadUtil
import com.likeminds.chatmm.utils.permissions.Permission
import com.likeminds.chatmm.utils.permissions.PermissionManager
import com.likeminds.chatmm.utils.permissions.PermissionTask

object MediaViewUtils {

    fun getOverflowMenu(
        context: Context,
        view: View,
        listener: PopupMenu.OnMenuItemClickListener,
    ): PopupMenu {
        val popupMenu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PopupMenu(context, view, Gravity.END, 0, R.style.PopupMenu)
        } else {
            PopupMenu(context, view, Gravity.END)
        }
        popupMenu.setOnMenuItemClickListener(listener)
        popupMenu.menuInflater.inflate(
            R.menu.media_horizontal_screen_menu,
            popupMenu.menu
        )
        return popupMenu
    }

    fun saveToGallery(
        lifecycleOwner: LifecycleOwner,
        activity: Activity,
        uri: Uri,
        notificationIcon: Int
    ) {
        if (activity !is BaseAppCompatActivity) {
            return
        }
        PermissionManager.performTaskWithPermission(
            activity,
            PermissionTask {
                val downloadObserver = DownloadUtil.startDownload(
                    activity,
                    uri.toString(),
                    notificationIcon
                )
                if (downloadObserver == null) {
                    ViewUtils.showShortToast(
                        activity,
                        activity.getString(R.string.media_is_downloading)
                    )
                    return@PermissionTask
                }
                val type = uri.getMediaType(activity)
                downloadObserver.observe(lifecycleOwner) { state ->
                    when (state) {
                        WorkInfo.State.ENQUEUED -> {
                            if (type == VIDEO && lifecycleOwner.lifecycle.currentState.isAtLeast(
                                    Lifecycle.State.RESUMED
                                )
                            ) {
                                ViewUtils.showShortToast(
                                    activity,
                                    activity.getString(R.string.downloading_video)
                                )
                            }
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                val toastMessage = DownloadUtil.getToastMessage(
                                    activity,
                                    type
                                )
                                ViewUtils.showShortToast(activity, toastMessage)
                            }
                        }
                        else -> {
                            Log.i("saveToGallery", "Worker State : $state")
                        }
                    }
                }
            },
            Permission.getStoragePermissionData(),
            showInitialPopup = true,
            showDeniedPopup = true
        )
    }
}