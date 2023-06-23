package com.likeminds.chatmm.media.view

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class MediaActivity : BaseAppCompatActivity() {

    companion object {
        const val BUNDLE_MEDIA_EXTRAS = "BUNDLE_MEDIA_EXTRAS"
        const val ARG_MEDIA_EXTRAS = "media_extras"
        const val ARG_SINGLE_URI_DATA = "singleUriData"
        const val ARG_IS_FROM_ACTIVITY = "is_from_activity"
        const val ARG_CROP_SQUARE = "crop_square"

        @JvmStatic
        fun startActivity(context: Context, mediaExtras: MediaExtras) {
            val intent = Intent(context, MediaActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_EXTRAS, mediaExtras)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(
            context: Context,
            mediaExtras: MediaExtras,
            clipData: ClipData? = null,
        ): Intent {
            val intent = Intent(context, MediaActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_EXTRAS, mediaExtras)
            intent.putExtras(bundle)
            if (clipData != null) {
                intent.clipData = clipData
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }
    }
}