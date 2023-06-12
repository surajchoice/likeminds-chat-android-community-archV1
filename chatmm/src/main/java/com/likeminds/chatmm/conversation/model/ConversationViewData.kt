package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import kotlinx.parcelize.Parcelize

@Parcelize
class ConversationViewData private constructor(

) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = TODO("Not yet implemented")
}