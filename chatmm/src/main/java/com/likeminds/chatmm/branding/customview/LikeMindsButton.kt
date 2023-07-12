package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsButton : MaterialButton {
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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsButton)
        val fontStyle = array.getString(R.styleable.LikeMindsButton_font_type)
        val drawableType = array.getString(R.styleable.LikeMindsButton_drawable_type)
        val buttonType = array.getString(R.styleable.LikeMindsButton_button_type)
        val textType = array.getString(R.styleable.LikeMindsButton_text_type)
        typeface = BrandingUtil.getTypeFace(context, fontStyle)
        array.recycle()

        // applies button color to button drawables
        if (!drawableType.equals("normal")) {
            compoundDrawables.forEach {
                it?.setTintList(ColorStateList.valueOf(LMBranding.getButtonsColor()))
            }
        }

        // bg color
        if (!buttonType.equals("normal")) {
            backgroundTintList = ColorStateList.valueOf(LMBranding.getButtonsColor())
        }

        // text color
        if (!textType.equals("normal")) {
            setTextColor(LMBranding.getButtonsColor())
        }
    }
}