package com.likeminds.chatmm.utils.permissions

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.databinding.DialogPermissionBinding
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.permissions.PermissionManager.Companion.REQUEST_CODE_SETTINGS_PERMISSION

class PermissionDialog(
    private val activity: BaseAppCompatActivity,
    private val task: PermissionTask,
    private val permission: Permission,
    private val mode: Mode,
    private val permissionDeniedCallback: PermissionDeniedCallback?,
) : Dialog(activity), View.OnClickListener {
    private val dialogPermissionBinding: DialogPermissionBinding =
        DialogPermissionBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(dialogPermissionBinding.root)

        //set branding to view
        dialogPermissionBinding.imageViewIcon.setBackgroundColor(LMBranding.getButtonsColor())
        dialogPermissionBinding.textViewPositiveButton.setTextColor(LMBranding.getButtonsColor())

        dialogPermissionBinding.imageViewIcon.setImageDrawable(
            ContextCompat.getDrawable(
                activity,
                permission.dialogImage
            )
        )
        when (mode) {
            Mode.INIT -> {
                dialogPermissionBinding.textViewMessage.text = permission.preDialogMessage
                dialogPermissionBinding.textViewPositiveButton.text =
                    activity.getString(R.string.permission_continue)
            }
            Mode.DENIED -> {
                dialogPermissionBinding.textViewMessage.text = permission.deniedDialogMessage
                dialogPermissionBinding.textViewPositiveButton.text =
                    activity.getString(R.string.settings)
            }
        }
        dialogPermissionBinding.textViewPositiveButton.setOnClickListener(this)
        dialogPermissionBinding.textViewNegativeButton.setOnClickListener(this)
        if (permissionDeniedCallback != null) {
            setOnCancelListener { permissionDeniedCallback.onDeny() }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textViewPositiveButton -> {
                when (mode) {
                    Mode.INIT ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            activity.requestPermission(permission, object : PermissionCallback {
                                override fun onGrant() {
                                    task.doTask()
                                }

                                override fun onDeny() {
                                    permissionDeniedCallback?.onDeny()
                                }
                            })
                        }
                    Mode.DENIED -> {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivityForResult(intent, REQUEST_CODE_SETTINGS_PERMISSION)
                    }
                }
                dismiss()
            }
            R.id.textViewNegativeButton -> {
                permissionDeniedCallback?.onCancel()
                dismiss()
            }
        }
    }

    enum class Mode {
        INIT, DENIED
    }

    override fun onStart() {
        super.onStart()
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
