package com.likeminds.chatmm.branding.customview.snackbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.likeminds.chatmm.R
import com.likeminds.chatmm.utils.ViewUtils.findSuitableParent

class LikeMindsSnackbar(
    parent: ViewGroup,
    content: LikeMindsSnackbarView
) : BaseTransientBottomBar<LikeMindsSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {
        fun make(view: View, text: String): LikeMindsSnackbar {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.likeminds_snackbar,
                parent,
                false
            ) as LikeMindsSnackbarView

            customView.textView.text = text

            return LikeMindsSnackbar(
                parent,
                customView
            )
        }
    }
}