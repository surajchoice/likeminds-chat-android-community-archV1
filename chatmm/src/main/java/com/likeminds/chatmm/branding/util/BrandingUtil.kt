package com.likeminds.chatmm.branding.util

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.branding.model.LMBranding

object BrandingUtil {
    /**
     * @param context - context to retrieve assets
     * @param fontStyle - style of font to be applied
     * @return Typeface? - typeface of current font as per the [fontStyle]
     * */
    fun getTypeFace(context: Context, fontStyle: String?): Typeface? {
        val currentFont = LMBranding.getCurrentFonts()

        val typeface = when (fontStyle) {
            "bold" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.bold)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_bold)
                }
            }
            "medium" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.medium)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_medium)
                }
            }
            "regular" -> {
                if (currentFont != null) {
                    Typeface.createFromAsset(context.assets, currentFont.regular)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_regular)
                }
            }
            else -> {
                ResourcesCompat.getFont(context, R.font.roboto_regular)
            }
        }
        return typeface
    }

    /**
     * @param defaultColor - color already set to the view
     * @param textType - type of text
     * @return Int - integer color
     * */
    fun getTextColor(defaultColor: Int, textType: String?): Int {
        val color = when (textType) {
            "title" -> {
                LMBranding.getToolbarColor()
            }
            "subtitle" -> {
                LMBranding.getSubtitleColor()
            }
            "special" -> {
                LMBranding.getButtonsColor()
            }
            else -> {
                defaultColor
            }
        }
        return color
    }
}