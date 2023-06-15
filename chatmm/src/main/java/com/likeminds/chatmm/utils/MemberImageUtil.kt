package com.likeminds.chatmm.utils

import android.widget.ImageView
import com.likeminds.chatmm.utils.databinding.ImageBindingUtil
import com.likeminds.chatmm.utils.generator.ColorGenerator
import com.likeminds.chatmm.utils.generator.TextDrawable

object MemberImageUtil {

    val SIXTY_PX = ViewUtils.dpToPx(60)

    fun setImage(
        imageUrl: String?,
        name: String?,
        id: String?,
        imageView: ImageView,
        showGreyScale: Boolean = false,
        showRoundImage: Boolean = false,
        showRectRoundImage: Boolean = false,
        cornerRadius: Int = 0,
        objectKey: Any? = null
    ): Int {
        val nameDrawable = getNameDrawable(SIXTY_PX, id, name, showRoundImage, showRectRoundImage)
        ImageBindingUtil.loadImage(
            imageView,
            imageUrl,
            nameDrawable.first,
            showRoundImage,
            cornerRadius,
            showGreyScale = showGreyScale,
            objectKey = objectKey
        )
        return nameDrawable.second
    }

    fun getNameDrawable(
        size: Int,
        id: String?,
        name: String?,
        circle: Boolean? = false,
        roundRect: Boolean? = false,
        isChatroom: Boolean? = false
    ): Pair<TextDrawable, Int> {
        val uniqueId = id ?: name ?: "LM"
        val nameCode = if (isChatroom == true) {
            getChatroomInitial(name)
        } else {
            getNameInitial(name)
        }
        val color = ColorGenerator.MATERIAL.getColor(uniqueId)
        val builder =
            TextDrawable.builder().beginConfig().bold().height(size).width(size).endConfig()
        val drawable = when {
            circle == true -> {
                builder.buildRound(nameCode, color)
            }
            roundRect == true -> {
                builder.buildRoundRect(nameCode, color, ViewUtils.dpToPx(10))
            }
            else -> {
                builder.buildRect(nameCode, color)
            }
        }
        return Pair(drawable, color)
    }

    private fun getNameInitial(
        userName: String?
    ): String {
        val name = userName?.trim()
        if (name.isNullOrEmpty()) {
            return "LM"
        }
        if (!name.contains(" ")) {
            return name[0].uppercase()
        }
        val nameParts = name.split(" ").map { it.trim() }
        return "${nameParts.first()[0].uppercase()}${nameParts.last()[0].uppercase()}"
    }

    private fun getChatroomInitial(
        chatroomName: String?
    ): String {
        if (chatroomName.isNullOrEmpty()) {
            return "LM"
        }
        return chatroomName[0].uppercase()
    }
}