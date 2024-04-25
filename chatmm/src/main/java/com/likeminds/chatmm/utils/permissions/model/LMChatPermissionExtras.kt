package com.likeminds.chatmm.utils.permissions.model

import androidx.annotation.DrawableRes
import com.likeminds.chatmm.R

class LMChatPermissionExtras private constructor(
    val permissions: Array<String>,
    val requestCode: Int,
    val preDialogMessage: String,
    val deniedDialogMessage: String,
    @param:DrawableRes @field:DrawableRes
    val dialogImage: Int
) {
    class Builder {
        private var permissions: Array<String> = emptyArray()
        private var requestCode: Int = 0
        private var preDialogMessage: String = ""
        private var deniedDialogMessage: String = ""
        private var dialogImage: Int = R.drawable.lm_chat_ic_folder

        fun permissions(permissions: Array<String>) = apply { this.permissions = permissions }
        fun requestCode(requestCode: Int) = apply { this.requestCode = requestCode }
        fun preDialogMessage(preDialogMessage: String) =
            apply { this.preDialogMessage = preDialogMessage }

        fun deniedDialogMessage(deniedDialogMessage: String) =
            apply { this.deniedDialogMessage = deniedDialogMessage }

        fun dialogImage(dialogImage: Int) = apply { this.dialogImage = dialogImage }

        fun build() = LMChatPermissionExtras(
            permissions,
            requestCode,
            preDialogMessage,
            deniedDialogMessage,
            dialogImage
        )
    }

    fun toBuilder(): Builder {
        return Builder().permissions(permissions)
            .requestCode(requestCode)
            .preDialogMessage(preDialogMessage)
            .deniedDialogMessage(deniedDialogMessage)
            .dialogImage(dialogImage)
    }
}