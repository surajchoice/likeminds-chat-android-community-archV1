package com.likeminds.chatmm.branding.model

import android.graphics.Color

// responsible for all the branding-related things like colors and fonts
object LMBranding {

    private var headerColor: String = "#FFFFFF"
    private var buttonsColor: String = "#5046E5"
    private var textLinkColor: String = "#007AFF"
    private var fonts: LMFonts? = null

    /**
     * @param setBrandingRequest - Request to set branding with colors and fonts
     * sets headerColor, buttonsColor, textLinkColor and fonts, used throughout the app
     * */
    fun setBranding(setBrandingRequest: SetBrandingRequest) {
        headerColor = setBrandingRequest.headerColor
        buttonsColor = setBrandingRequest.buttonsColor
        textLinkColor = setBrandingRequest.textLinkColor
        fonts = setBrandingRequest.fonts
    }

    // returns button color
    fun getButtonsColor(): Int {
        return Color.parseColor(buttonsColor)
    }

    // returns header color
    fun getHeaderColor(): Int {
        return Color.parseColor(headerColor)
    }

    // returns text link color
    fun getTextLinkColor(): Int {
        return Color.parseColor(textLinkColor)
    }

    // returns toolbar color
    fun getToolbarColor(): Int {
        return if (headerColor == "#FFFFFF") {
            Color.BLACK
        } else {
            Color.WHITE
        }
    }

    // returns color of subtitle text
    fun getSubtitleColor(): Int {
        return if (headerColor == "#FFFFFF") {
            Color.GRAY
        } else {
            Color.WHITE
        }
    }

    // returns paths of the current fonts
    fun getCurrentFonts(): LMFonts? {
        return fonts
    }
}