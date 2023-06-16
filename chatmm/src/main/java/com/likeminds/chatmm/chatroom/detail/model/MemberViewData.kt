package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_MEMBER
import kotlinx.parcelize.Parcelize

// todo: removed question view data
@Parcelize
class MemberViewData private constructor(
    val id: String?,
    val uid: String?,
    val name: String?,
    val email: String?,
    val headline: String?,
    val imageUrl: String?,
    val dynamicViewType: Int,
    val state: Int,
    val communityId: String?,
    val communityName: String?,
    val removeState: Int?,
    val isGuest: Boolean?,
    val isOwner: Boolean?,
    val hideBottomLine: Boolean?,
    val customTitle: String?,
    val customIntroText: String?,
    val customClickText: String?,
    val memberSince: String?,
    val listOfMenu: List<MemberActionViewData>?,
    val parentChatroom: ChatroomViewData?,
    val parentViewItemPosition: Int?,
    val showMoreView: Boolean?,
    val screenType: Int?,
    val updatedAt: Long?,
    val userUniqueId: String?
) : Parcelable, BaseViewType {
    override val viewType: Int
        get() = dynamicViewType

    class Builder {
        private var id: String? = null
        private var uid: String? = null
        private var name: String? = null
        private var email: String? = null
        private var headline: String? = null
        private var imageUrl: String? = null
        private var dynamicViewType: Int = ITEM_MEMBER
        private var state: Int = STATE_NOTHING
        private var communityId: String? = null
        private var removeState: Int? = null
        private var isGuest: Boolean? = null
        private var hideBottomLine: Boolean? = null
        private var customIntroText: String? = null
        private var customClickText: String? = null
        private var memberSince: String? = null
        private var communityName: String? = null
        private var isOwner: Boolean? = null
        private var customTitle: String? = null
        private var listOfMenu: List<MemberActionViewData>? = null
        private var parentChatroom: ChatroomViewData? = null
        private var parentViewItemPosition: Int? = null
        private var showMoreView: Boolean? = null
        private var screenType: Int? = null
        private var updatedAt: Long? = null
        private var userUniqueId: String? = null

        fun id(id: String?) = apply { this.id = id }
        fun uid(uid: String?) = apply { this.uid = uid }
        fun name(name: String?) = apply { this.name = name }
        fun email(email: String?) = apply { this.email = email }
        fun headline(headline: String?) = apply { this.headline = headline }
        fun imageUrl(imageUrl: String?) = apply { this.imageUrl = imageUrl }
        fun dynamicViewType(dynamicViewType: Int) = apply { this.dynamicViewType = dynamicViewType }
        fun state(state: Int) = apply { this.state = state }
        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun communityName(communityName: String?) = apply { this.communityName = communityName }

        fun removeState(removeState: Int?) = apply { this.removeState = removeState }
        fun isGuest(isGuest: Boolean?) = apply { this.isGuest = isGuest }
        fun isOwner(isOwner: Boolean?) = apply { this.isOwner = isOwner }
        fun hideBottomLine(hideBottomLine: Boolean?) =
            apply { this.hideBottomLine = hideBottomLine }

        fun customTitle(customTitle: String?) = apply { this.customTitle = customTitle }
        fun customIntroText(customIntroText: String?) =
            apply { this.customIntroText = customIntroText }

        fun customClickText(customClickText: String?) =
            apply { this.customClickText = customClickText }

        fun memberSince(memberSince: String?) = apply { this.memberSince = memberSince }
        fun listOfMenu(listOfMenu: List<MemberActionViewData>?) =
            apply { this.listOfMenu = listOfMenu }

        fun parentChatroom(parentChatroom: ChatroomViewData?) =
            apply { this.parentChatroom = parentChatroom }

        fun parentViewItemPosition(parentViewItemPosition: Int?) =
            apply { this.parentViewItemPosition = parentViewItemPosition }

        fun showMoreView(showMoreView: Boolean?) = apply { this.showMoreView = showMoreView }
        fun screenType(screenType: Int?) = apply { this.screenType = screenType }
        fun updatedAt(updatedAt: Long?) = apply { this.updatedAt = updatedAt }

        fun userUniqueId(userUniqueId: String?) = apply { this.userUniqueId = userUniqueId }

        fun build() = MemberViewData(
            id,
            uid,
            name,
            email,
            headline,
            imageUrl,
            dynamicViewType,
            state,
            communityId,
            communityName,
            removeState,
            isGuest,
            isOwner,
            hideBottomLine,
            customTitle,
            customIntroText,
            customClickText,
            memberSince,
            listOfMenu,
            parentChatroom,
            parentViewItemPosition,
            showMoreView,
            screenType,
            updatedAt,
            userUniqueId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .uid(uid)
            .name(name)
            .email(email)
            .headline(headline)
            .imageUrl(imageUrl)
            .dynamicViewType(dynamicViewType)
            .state(state)
            .communityId(communityId)
            .communityName(communityName)
            .removeState(removeState)
            .isGuest(isGuest)
            .isOwner(isOwner)
            .hideBottomLine(hideBottomLine)
            .customTitle(customTitle)
            .customIntroText(customIntroText)
            .customClickText(customClickText)
            .memberSince(memberSince)
            .listOfMenu(listOfMenu)
            .parentChatroom(parentChatroom)
            .parentViewItemPosition(parentViewItemPosition)
            .showMoreView(showMoreView)
            .screenType(screenType)
            .updatedAt(updatedAt)
            .userUniqueId(userUniqueId)
    }
}