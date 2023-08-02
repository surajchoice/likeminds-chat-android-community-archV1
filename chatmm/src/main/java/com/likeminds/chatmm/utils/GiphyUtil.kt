package com.likeminds.chatmm.utils

import com.giphy.sdk.core.models.Media

object GiphyUtil {

    fun getGifLink(media: Media): String? {
        return when {
            !media.images.preview?.gifUrl.isNullOrEmpty() -> media.images.preview?.gifUrl
            !media.images.fixedWidth?.gifUrl.isNullOrEmpty() -> media.images.fixedWidth?.gifUrl
            !media.images.fixedHeight?.gifUrl.isNullOrEmpty() -> media.images.fixedHeight?.gifUrl
            !media.images.original?.gifUrl.isNullOrEmpty() -> media.images.original?.gifUrl
            !media.images.fixedWidthSmall?.gifUrl.isNullOrEmpty() -> media.images.fixedWidthSmall?.gifUrl
            !media.images.fixedHeightSmall?.gifUrl.isNullOrEmpty() -> media.images.fixedHeightSmall?.gifUrl
            !media.images.downsizedSmall?.gifUrl.isNullOrEmpty() -> media.images.downsizedSmall?.gifUrl
            !media.images.downsizedMedium?.gifUrl.isNullOrEmpty() -> media.images.downsizedMedium?.gifUrl
            !media.images.downsizedLarge?.gifUrl.isNullOrEmpty() -> media.images.downsizedLarge?.gifUrl
            !media.images.downsized?.gifUrl.isNullOrEmpty() -> media.images.downsized?.gifUrl
            else -> null
        }
    }
}