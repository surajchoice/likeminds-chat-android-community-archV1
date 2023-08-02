package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.checkbox.MaterialCheckBox
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsCheckbox : MaterialCheckBox {
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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsCheckbox)
        typeface = BrandingUtil.getTypeFace(
            context,
            array.getString(R.styleable.LikeMindsCheckbox_set_font_type)
        )
        array.recycle()
    }
}