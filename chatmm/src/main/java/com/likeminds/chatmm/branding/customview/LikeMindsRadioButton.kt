package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsRadioButton : AppCompatRadioButton {
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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsRadioButton)
        typeface = BrandingUtil.getTypeFace(
            context,
            array.getString(R.styleable.LikeMindsRadioButton_set_font_style)
        )
        array.recycle()
    }
}