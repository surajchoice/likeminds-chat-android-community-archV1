package com.likeminds.chatmm.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.customview.snackbar.LikeMindsSnackbar
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

    fun showKeyboard(context: Context, editText: EditText) {
        editText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

    // shows short snack bar with message
    fun showShortSnack(view: View, text: String?, anchorView: View? = null) {
        if (text.isNullOrEmpty()) return
        val snackBar = LikeMindsSnackbar.make(view, text)
        anchorView?.let {
            snackBar.setAnchorView(anchorView)
        }
        snackBar.show()
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
     * set chatroom image to the [ivChatroomImage]
     **/
    fun setChatroomImage(
        chatroomId: String,
        chatroomHeader: String?,
        chatroomImageUrl: String?,
        ivChatroomImage: ImageView,
    ) {
        val nameDrawable = MemberImageUtil.getNameDrawable(
            MemberImageUtil.SIXTY_PX,
            id = chatroomId,
            name = chatroomHeader,
            circle = true,
            isChatroom = true
        )
        ImageBindingUtil.loadImage(
            ivChatroomImage,
            chatroomImageUrl,
            nameDrawable.first,
            isCircle = true,
        )
    }

    fun getFragmentVisible(fragment: Fragment?): Boolean {
        return fragment?.isVisible == true
    }

    fun View.startRevealAnimation(originView: View) {
        val originalPos = IntArray(2)
        originView.getLocationInWindow(originalPos)
        val radius = resources.displayMetrics.widthPixels

        val anim = ViewAnimationUtils.createCircularReveal(
            this,
            (originalPos[0] + (originView.width / 2)), (originalPos[1] + (originView.height / 2)),
            0F, radius.toFloat()
        )
        this.visibility = View.VISIBLE
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 300L
        anim.start()
    }

    fun View.endRevealAnimation(originView: View, cb: () -> Unit) {
        val targetView = this
        val originalPos = IntArray(2)
        originView.getLocationInWindow(originalPos)
        val radius = resources.displayMetrics.widthPixels

        val anim = ViewAnimationUtils.createCircularReveal(
            targetView,
            (originalPos[0] + (originView.width / 2)), (originalPos[1] + (originView.height / 2)),
            radius.toFloat(), 0F
        )
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 300L
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                cb()
                targetView.visibility = View.GONE
            }
        })
        anim.start()
    }
}