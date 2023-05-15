package com.likeminds.chatmm.utils.customview

import android.annotation.TargetApi
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.utils.connectivity.ConnectivityBroadcastReceiver
import com.likeminds.chatmm.utils.connectivity.ConnectivityReceiverListener
import com.likeminds.chatmm.utils.permissions.Permission
import com.likeminds.chatmm.utils.permissions.PermissionCallback
import com.likeminds.chatmm.utils.permissions.SessionPermission
import com.likeminds.chatmm.utils.snackbar.CustomSnackBar
import javax.inject.Inject

open class BaseAppCompatActivity : ConnectivityReceiverListener, AppCompatActivity() {
    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */

    @Inject
    lateinit var snackBar: CustomSnackBar

    private lateinit var sessionPermission: SessionPermission
    private val permissionCallbackSparseArray = SparseArray<PermissionCallback>()

    private var wasNetworkGone = false

    private val connectivityBroadcastReceiver by lazy {
        ConnectivityBroadcastReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionPermission = SessionPermission(application)
    }

    override fun onResume() {
        super.onResume()
        connectivityBroadcastReceiver.setListener(this)
        registerReceiver(
            connectivityBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onStart() {
        super.onStart()
        setStatusBarColor(LMBranding.getHeaderColor())
    }

    override fun onPause() {
        super.onPause()
    }

    private fun setStatusBarColor(statusBarColor: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun hasPermission(permission: Permission): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            true
        } else {
            checkSelfPermission(permission.permissionName) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPermission(permission: Permission, permissionCallback: PermissionCallback) {
        permissionCallbackSparseArray.put(permission.requestCode, permissionCallback)
        sessionPermission.setPermissionRequest(permission)
        requestPermissions(arrayOf(permission.permissionName), permission.requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun canRequestPermission(permission: Permission): Boolean {
        return !wasRequestedBefore(permission) ||
                shouldShowRequestPermissionRationale(permission.permissionName)
    }

    private fun wasRequestedBefore(permission: Permission): Boolean {
        return sessionPermission.wasPermissionRequestedBefore(permission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val callback = permissionCallbackSparseArray.get(requestCode, null) ?: return
        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onGrant()
            } else {
                callback.onDeny()
            }
        } else {
            callback.onDeny()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val parentView = findViewById<ViewGroup>(android.R.id.content) ?: return
        if (parentView.childCount > 0) {
            parentView.getChildAt(0)?.let { view ->
                if (isConnected && wasNetworkGone) {
                    wasNetworkGone = false
                    snackBar.showMessage(view, "Internet connection restored", true)
                }
                if (!isConnected) {
                    wasNetworkGone = true
                    snackBar.showNoInternet(view)
                }
            }
        }
    }
}