package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.likeminds.chatmm.branding.model.LMBranding

class LikeMindsToolbar : Toolbar {
    constructor(context: Context) : super(context) {
        initiate()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initiate()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initiate()
    }

    private fun initiate() {
        // background color
        this.setBackgroundColor(LMBranding.getHeaderColor())

        // toolbar color
        val color = LMBranding.getToolbarColor()
        this.overflowIcon?.setTint(color)
        this.navigationIcon?.setTint(color)
    }
}