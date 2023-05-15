package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsExtendedFAB : ExtendedFloatingActionButton {
    constructor(context: Context) : super(context) {
        initiate(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initiate(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initiate(attrs)
    }

    private fun initiate(attrs: AttributeSet?) {
        // fonts
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsExtendedFAB)
        val fontStyle = array.getString(R.styleable.LikeMindsExtendedFAB_font_Style)
        this.typeface = BrandingUtil.getTypeFace(context, fontStyle)
        array.recycle()

        // color
        this.backgroundTintList = ColorStateList.valueOf(LMBranding.getButtonsColor())
    }
}