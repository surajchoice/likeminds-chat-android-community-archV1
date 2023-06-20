package com.likeminds.chatmm.utils

import android.util.Patterns
import android.webkit.URLUtil
import com.likeminds.chatmm.media.model.*

object ValueUtils {
    fun String?.containsUrl(): Boolean {
        this?.let {
            return Patterns.WEB_URL.matcher(this).matches()
        }
        return false
    }

    fun Int.isValidIndex(itemCount: Int): Boolean {
        return this > -1 && this < itemCount
    }

    fun Int.isValidIndex(items: List<*>? = null): Boolean {
        return if (items != null) {
            this > -1 && this < items.size
        } else {
            this > -1
        }
    }

    fun <T> List<T>.getItemInList(position: Int): T? {
        if (position < 0 || position >= this.size) {
            return null
        }
        return this[position]
    }

    fun String.getEmailIfExist(): String? {
        return try {
            val emails: MutableList<String> = ArrayList()
            val matcher = Patterns.EMAIL_ADDRESS.matcher(this)
            while (matcher.find()) {
                val email = matcher.group()
                emails.add(email)
            }
            if (emails.isNotEmpty()) {
                emails.first()
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    fun String.getUrlIfExist(): String? {
        return try {
            val links: MutableList<String> = ArrayList()
            val matcher = Patterns.WEB_URL.matcher(this)
            while (matcher.find()) {
                val link = matcher.group()
                if (URLUtil.isValidUrl(link)) {
                    links.add(link)
                } else {
                    val newHttpsLink = "https://$link"
                    if (URLUtil.isValidUrl(newHttpsLink)) {
                        links.add(newHttpsLink)
                    }
                }
            }
            if (links.isNotEmpty()) {
                links.first()
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Check mediaType from Media mimeType
     * */
    fun String?.getMediaType(): String? {
        var mediaType: String? = null
        if (this != null) {
            when {
                this == "image/gif" -> mediaType = GIF
                this.startsWith("image") -> mediaType = IMAGE
                this.startsWith("video") -> mediaType = VIDEO
                this == "application/pdf" -> mediaType = PDF
                this.startsWith("audio") -> mediaType = AUDIO
            }
        }
        return mediaType
    }

    fun Int?.getMaxCountNumberText(): String {
        if (this == null) {
            return "0"
        }
        return if (this > 99) "99+" else this.toString()
    }
}