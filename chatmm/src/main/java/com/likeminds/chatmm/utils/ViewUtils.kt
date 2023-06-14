package com.likeminds.chatmm.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.likeminds.chatmm.R
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil

//view related utils class
object ViewUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun showShortToast(context: Context?, text: String?) {
        if (context == null || text.isNullOrEmpty()) return
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    // shows short toast with "Something went wrong!" message
    fun showSomethingWentWrongToast(context: Context) {
        showShortToast(context, context.getString(R.string.something_went_wrong))
    }

    // shows short toast with error message
    fun showErrorMessageToast(context: Context, errorMessage: String?) {
        showShortToast(context, errorMessage ?: context.getString(R.string.something_went_wrong))
    }

    //find parent for a particular view
    fun View?.findSuitableParent(): ViewGroup? {
        var view = this
        var fallback: ViewGroup? = null
        do {
            if (view is CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return view
            } else if (view is FrameLayout) {
                if (view.id == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return view
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = view
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        } while (view != null)

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback
    }

    /**
     * set chatroom image to the [ivChatroomImage] and
     * return [onChatroomImagePresent] if [chatroomImageUrl] != null
     * and [onChatroomImageNotPresent] if [chatroomImageUrl] == null
     **/
    fun setChatroomImage(
        chatroomImageUrl: String?,
        ivChatroomImage: ImageView,
        onChatroomImagePresent: () -> Unit,
        onChatroomImageNotPresent: () -> Unit
    ) {
        if (chatroomImageUrl != null) {
            onChatroomImagePresent()
            ImageBindingUtil.loadImage(
                ivChatroomImage,
                chatroomImageUrl,
                isCircle = true,
                objectKey = chatroomImageUrl
            )
        } else {
            onChatroomImageNotPresent()
        }
    }
}