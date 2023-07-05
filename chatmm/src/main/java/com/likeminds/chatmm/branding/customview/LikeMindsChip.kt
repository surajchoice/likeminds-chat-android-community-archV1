package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsChip : Chip {
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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsChip)
        typeface = BrandingUtil.getTypeFace(
            context,
            array.getString(R.styleable.LikeMindsChip_font_look)
        )
        array.recycle()
    }
}