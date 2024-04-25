package com.likeminds.chatmm.branding.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.branding.util.BrandingUtil

internal class LikeMindsTextView : AppCompatTextView {

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
        val array = context.obtainStyledAttributes(attrs, R.styleable.LikeMindsTextView)
        val drawableType = array.getString(R.styleable.LikeMindsButton_drawable_type)

        typeface = BrandingUtil.getTypeFace(
            context,
            array.getString(R.styleable.LikeMindsTextView_fontType)
        )

        //text color
        setTextColor(
            BrandingUtil.getTextColor(
                this.currentTextColor,
                array.getString(R.styleable.LikeMindsTextView_textType)
            )
        )

        // sets text link color
        setLinkTextColor(LMBranding.getTextLinkColor())

        // applies button color to button drawables
        if (drawableType.equals("special")) {
            compoundDrawables.forEach {
                it?.setTintList(ColorStateList.valueOf(LMBranding.getButtonsColor()))
            }
        }

        array.recycle()
    }
}