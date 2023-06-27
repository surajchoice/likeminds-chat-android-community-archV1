package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.media.model.InternalMediaType
import com.likeminds.chatmm.media.model.MEDIA_RESULT_BROWSE
import com.likeminds.chatmm.media.model.MediaPickerExtras
import com.likeminds.chatmm.media.model.MediaPickerResult
import com.likeminds.chatmm.utils.ViewUtils.currentFragment
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.permissions.Permission
import com.likeminds.chatmm.utils.permissions.PermissionDeniedCallback
import com.likeminds.chatmm.utils.permissions.PermissionManager

class MediaPickerActivity : BaseAppCompatActivity() {

    private lateinit var mediaPickerExtras: MediaPickerExtras

    companion object {
        const val PICK_MEDIA = 5001
        const val BROWSE_MEDIA = 5002
        const val BROWSE_DOCUMENT = 5003
        const val PICK_CAMERA = 5004
        const val CROP_IMAGE = 5005

        private const val ARG_MEDIA_PICKER_EXTRAS = "mediaPickerExtras"
        const val ARG_MEDIA_PICKER_RESULT = "mediaPickerResult"

        fun start(context: Context, extras: MediaPickerExtras) {
            val intent = Intent(context, MediaPickerActivity::class.java)
            intent.apply {
                putExtras(Bundle().apply {
                    putParcelable(ARG_MEDIA_PICKER_EXTRAS, extras)
                })
            }
            context.startActivity(intent)
        }

        fun getIntent(context: Context, extras: MediaPickerExtras): Intent {
            return Intent(context, MediaPickerActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putParcelable(ARG_MEDIA_PICKER_EXTRAS, extras)
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKApplication.getInstance().mediaComponent()?.inject(this)

        setContentView(R.layout.activity_media_picker)
        val extras = intent.extras?.getParcelable<MediaPickerExtras>(ARG_MEDIA_PICKER_EXTRAS)
        if (extras == null) {
            throw IllegalArgumentException("Arguments are missing")
        } else {
            mediaPickerExtras = extras
        }

        checkStoragePermission()
    }

    private fun checkStoragePermission() {
        PermissionManager.performTaskWithPermission(
            this,
            { startMediaPickerFragment() },
            Permission.getStoragePermissionData(),
            showInitialPopup = true,
            showDeniedPopup = true,
            permissionDeniedCallback = object : PermissionDeniedCallback {
                override fun onDeny() {
                    onBackPressed()
                }

                override fun onCancel() {
                    onBackPressed()
                }
            }
        )
    }

    private fun startMediaPickerFragment() {
        checkIfDocumentPickerInitiated()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment ?: return
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_media_picker_graph)
        val navController = navHostFragment.navController

        when {
            InternalMediaType.isImageOrVideo(mediaPickerExtras.mediaTypes) -> {
                navGraph.setStartDestination(R.id.mediaPickerFolderFragment)
            }
            InternalMediaType.isPDF(mediaPickerExtras.mediaTypes) -> {
                navGraph.setStartDestination(R.id.mediaPickerDocumentFragment)
            }
            InternalMediaType.isAudio(mediaPickerExtras.mediaTypes) -> {
                navGraph.setStartDestination(R.id.mediaPickerAudioFragment)
            }
            else -> {
                finish()
            }
        }
        val args = Bundle().apply {
            putParcelable(ARG_MEDIA_PICKER_EXTRAS, mediaPickerExtras)
        }
        navController.setGraph(navGraph, args)
    }

    /**
     * If Media Picker type is Pdf and device version is >= Q(29), then show system app picker.
     * This is done due to storage restrictions for non-media files in Android 10.
     * */
    private fun checkIfDocumentPickerInitiated() {
        if (InternalMediaType.isPDF(mediaPickerExtras.mediaTypes)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        ) {
            val intent = Intent().apply {
                putExtras(Bundle().apply {
                    putParcelable(
                        ARG_MEDIA_PICKER_RESULT, MediaPickerResult.Builder()
                            .mediaPickerResultType(MEDIA_RESULT_BROWSE)
                            .mediaTypes(mediaPickerExtras.mediaTypes)
                            .allowMultipleSelect(mediaPickerExtras.allowMultipleSelect)
                            .build()
                    )
                })
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionManager.REQUEST_CODE_SETTINGS_PERMISSION) {
            checkStoragePermission()
        }
    }

    override fun onBackPressed() {
        when (val fragment = supportFragmentManager.currentFragment(R.id.nav_host)) {
            is MediaPickerFolderFragment -> {
                super.onBackPressed()
            }
            is MediaPickerItemFragment -> {
                fragment.onBackPressedFromFragment()
            }
            is MediaPickerDocumentFragment -> {
                if (fragment.onBackPressedFromFragment()) super.onBackPressed()
            }
            is MediaPickerAudioFragment -> {
                if (fragment.onBackPress()) super.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}