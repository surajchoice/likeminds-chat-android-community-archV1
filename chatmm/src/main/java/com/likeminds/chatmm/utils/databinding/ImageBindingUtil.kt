package com.likeminds.chatmm.utils.databinding

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.signature.ObjectKey
import com.github.chrisbanes.photoview.PhotoView
import com.likeminds.chatmm.utils.ViewUtils

object ImageBindingUtil {
    /**
     * Use it to load images programmatically
     * @param view ImageView
     * @param file Supported types -> String, Uri, Drawable, Int resource, Bitmap
     * @param placeholder Placeholder and Error for this request | Supported types -> Drawable, Int resource
     * @param isCircle Is circle crop needed
     * @param cornerRadius Corner radius in dp.
     * @param showGreyScale Is greyscale image needed
     */
    @JvmStatic
    fun loadImage(
        view: ImageView,
        file: Any?,
        placeholder: Any? = null,
        isCircle: Boolean = false,
        cornerRadius: Int = 0,
        showGreyScale: Boolean = false,
        isBlur: Boolean = false,
        objectKey: Any? = null
    ) {
        if ((file == null && placeholder == null)
            || (file != null && file !is String &&
                    file !is Uri && file !is Drawable &&
                    file !is Int && file !is Bitmap)
        ) {
            return
        }
        if (isValidContextForGlide(view.context)) {
            var builder = Glide.with(view).load(file)

            //Signature
            if (objectKey != null) {
                builder = builder.signature(ObjectKey(objectKey))
            }

            //Set the placeholder
            if (placeholder != null && placeholder is Int) {
                builder = builder.placeholder(placeholder).error(placeholder)
            } else if (placeholder != null && placeholder is Drawable) {
                builder = builder.placeholder(placeholder).error(placeholder)
            }
            if (isCircle) {
                builder = builder.circleCrop()
            }

            if (cornerRadius > 0 && isBlur) {
                builder = builder.transform(
                    CenterCrop(),
                    BlurTransformation(view.context),
                    RoundedCorners(ViewUtils.dpToPx(cornerRadius))
                )
            } else if (cornerRadius > 0) {
                builder = builder.transform(
                    CenterCrop(), RoundedCorners(ViewUtils.dpToPx(cornerRadius))
                )
            } else if (isBlur) {
                builder = builder.transform(CenterCrop(), BlurTransformation(view.context))
            }

            builder.into(view)
            if (showGreyScale) {
                createImageFilter(view)
            } else {
                view.clearColorFilter()
            }
        }
    }

    private fun createImageFilter(view: View) {
        val colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
            setSaturation(0f)
        })
        if (view is ImageView) {
            view.colorFilter = colorFilter
        } else if (view is PhotoView) {
            view.colorFilter = colorFilter
        }
    }

    fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            if (context.isDestroyed || context.isFinishing) {
                return false
            }
        }
        return true
    }
}