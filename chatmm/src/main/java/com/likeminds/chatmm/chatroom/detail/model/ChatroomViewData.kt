package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import com.likeminds.likemindschat.chatroom.model.ChatRequestState
import com.likeminds.likemindschat.community.model.Member
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomViewData private constructor(
    val id: String,
    val memberViewData: MemberViewData,
    val title: String,
    val createdAt: Long,
    val alreadySeenFullConversation: Boolean?,
    val answerText: String?,
    val shortAnswer: String?,
    val state: Int?,
    val shareUrl: String?,
    val communityId: String,
    val communityName: String,
    val type: Int?,
    val memberState: Int?,
    val about: String?,
    val header: String?,
    val showFollowTelescope: Boolean?,
    val showFollowAutoTag: Boolean?,
    val cardCreationTime: String?,
    val participantsCount: Int,
    val totalResponseCount: Int,
    val totalAllResponseCount: Int,
    val muteStatus: Boolean,
    val followStatus: Boolean,
    val date: String?,
    val hideBottomLine: Boolean?,
    val isTagged: Boolean?,
    val isPending: Boolean?,
    val isPinned: Boolean?,
    val deletedBy: String?,
    val updatedAt: Long?,
    val draftConversation: String?,
    val isSecret: Boolean?,
    val isDisabled: Boolean?,
    val secretChatroomParticipants: List<Int>?,
    val secretChatroomLeft: Boolean?,
    val unseenCount: Int?,
    val isEdited: Boolean?,
    val topic: ConversationViewData?,
    val reactions: List<ReactionViewData>?,
    val memberCanMessage: Boolean?,
    val chatroomImageUrl: String?,
    val unreadConversationCount: Int?,
    val autoFollowDone: Boolean?,
    val dynamicViewType: Int?,
    val deletedByMember: MemberViewData?,
    val chatRequestState: ChatRequestState,
    val isPrivateMember: Boolean?,
    val chatRequestedById: String?,
    val chatRequestCreatedAt: Long?,
    val chatRequestedBy: MemberViewData?,
    val chatroomWithUser: MemberViewData?,
    val chatroomWithUserId: String?
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_HOME_CHAT_ROOM

    fun isDeleted(): Boolean {
        return deletedBy != null
    }

    fun hasTitle(): Boolean {
        return title.isNotEmpty()
    }

    class Builder {
        private var id: String = ""
        private var memberViewData: MemberViewData = MemberViewData.Builder().build()
        private var title: String = ""
        private var createdAt: Long = 0
        private var alreadySeenFullConversation: Boolean? = null
        private var answerText: String? = null
        private var shortAnswer: String? = null
        private var state: Int? = null
        private var shareUrl: String? = null
        private var communityId: String = ""
        private var communityName: String = ""
        private var type: Int? = null
        private var memberState: Int? = null
        private var about: String? = null
        private var header: String? = null
        private var showFollowTelescope: Boolean? = null
        private var showFollowAutoTag: Boolean? = null
        private var cardCreationTime: String? = null
        private var participantsCount: Int = 0
        private var totalResponseCount: Int = 0
        private var totalAllResponseCount: Int = 0
        private var muteStatus: Boolean = false
        private var followStatus: Boolean = false
        private var date: String? = null
        private var hideBottomLine: Boolean? = null
        private var isTagged: Boolean? = null
        private var isPending: Boolean? = null
        private var isPinned: Boolean? = null
        private var deletedBy: String? = null
        private var updatedAt: Long? = null
        private var draftConversation: String? = null
        private var isSecret: Boolean? = null
        private var isDisabled: Boolean? = null
        private var secretChatroomParticipants: List<Int>? = null
        private var secretChatroomLeft: Boolean? = null
        private var unseenCount: Int? = null
        private var isEdited: Boolean? = null
        private var topic: ConversationViewData? = null
        private var reactions: List<ReactionViewData>? = null
        private var memberCanMessage: Boolean? = null
        private var chatroomImageUrl: String? = null
        private var unreadConversationCount: Int? = null
        private var autoFollowDone: Boolean? = null
        private var dynamicViewType: Int? = null
        private var deletedByMember: MemberViewData? = null
        private var chatRequestState: ChatRequestState = ChatRequestState.NOTHING
        private var isPrivateMember: Boolean? = null
        private var chatRequestedById: String? = null
        private var chatRequestCreatedAt: Long? = null
        private var chatRequestedBy: MemberViewData? = null
        private var chatroomWithUser: MemberViewData? = null
        private var chatroomWithUserId: String? = null

        fun id(id: String) = apply { this.id = id }
        fun memberViewData(memberViewData: MemberViewData) =
            apply { this.memberViewData = memberViewData }

        fun title(title: String) = apply { this.title = title }
        fun createdAt(createdAt: Long) = apply { this.createdAt = createdAt }
        fun alreadySeenFullConversation(alreadySeenFullConversation: Boolean?) =
            apply { this.alreadySeenFullConversation = alreadySeenFullConversation }

        fun answerText(answerText: String?) = apply { this.answerText = answerText }
        fun shortAnswer(shortAnswer: String?) = apply { this.shortAnswer = shortAnswer }
        fun state(state: Int?) = apply { this.state = state }
        fun shareUrl(shareUrl: String?) = apply { this.shareUrl = shareUrl }
        fun communityId(communityId: String) = apply { this.communityId = communityId }
        fun communityName(communityName: String) = apply { this.communityName = communityName }
        fun type(type: Int?) = apply { this.type = type }
        fun memberState(memberState: Int?) = apply { this.memberState = memberState }
        fun about(about: String?) = apply { this.about = about }
        fun header(header: String?) = apply { this.header = header }
        fun showFollowTelescope(showFollowTelescope: Boolean?) =
            apply { this.showFollowTelescope = showFollowTelescope }

        fun showFollowAutoTag(showFollowAutoTag: Boolean?) =
            apply { this.showFollowAutoTag = showFollowAutoTag }

        fun cardCreationTime(cardCreationTime: String?) =
            apply { this.cardCreationTime = cardCreationTime }

        fun participantsCount(participantsCount: Int) =
            apply { this.participantsCount = participantsCount }

        fun totalResponseCount(totalResponseCount: Int) =
            apply { this.totalResponseCount = totalResponseCount }

        fun totalAllResponseCount(totalAllResponseCount: Int) =
            apply { this.totalAllResponseCount = totalAllResponseCount }

        fun muteStatus(muteStatus: Boolean) = apply { this.muteStatus = muteStatus }
        fun followStatus(followStatus: Boolean) = apply { this.followStatus = followStatus }
        fun date(date: String?) = apply { this.date = date }
        fun hideBottomLine(hideBottomLine: Boolean?) =
            apply { this.hideBottomLine = hideBottomLine }

        fun isTagged(isTagged: Boolean?) = apply { this.isTagged = isTagged }
        fun isPending(isPending: Boolean?) = apply { this.isPending = isPending }
        fun isPinned(isPinned: Boolean?) = apply { this.isPinned = isPinned }
        fun deletedBy(deletedBy: String?) = apply { this.deletedBy = deletedBy }
        fun updatedAt(updatedAt: Long?) = apply { this.updatedAt = updatedAt }
        fun draftConversation(draftConversation: String?) =
            apply { this.draftConversation = draftConversation }

        fun isSecret(isSecret: Boolean?) = apply { this.isSecret = isSecret }
        fun isDisabled(isDisabled: Boolean?) = apply { this.isDisabled = isDisabled }
        fun secretChatroomParticipants(secretChatroomParticipants: List<Int>?) =
            apply { this.secretChatroomParticipants = secretChatroomParticipants }

        fun secretChatroomLeft(secretChatroomLeft: Boolean?) =
            apply { this.secretChatroomLeft = secretChatroomLeft }

        fun unseenCount(unseenCount: Int?) = apply { this.unseenCount = unseenCount }
        fun isEdited(isEdited: Boolean?) = apply { this.isEdited = isEdited }
        fun topic(topic: ConversationViewData?) = apply { this.topic = topic }
        fun reactions(reactions: List<ReactionViewData>?) = apply { this.reactions = reactions }
        fun memberCanMessage(memberCanMessage: Boolean?) =
            apply { this.memberCanMessage = memberCanMessage }

        fun chatroomImageUrl(chatroomImageUrl: String?) =
            apply { this.chatroomImageUrl = chatroomImageUrl }

        fun unreadConversationCount(unreadConversationCount: Int?) =
            apply { this.unreadConversationCount = unreadConversationCount }

        fun autoFollowDone(autoFollowDone: Boolean?) =
            apply { this.autoFollowDone = autoFollowDone }

        fun dynamicViewType(dynamicViewType: Int?) =
            apply { this.dynamicViewType = dynamicViewType }

        fun deletedByMember(deletedByMember: MemberViewData?) =
            apply { this.deletedByMember = deletedByMember }

        fun chatRequestState(chatRequestState: ChatRequestState) =
            apply { this.chatRequestState = chatRequestState }

        fun isPrivateMember(isPrivateMember: Boolean?) =
            apply { this.isPrivateMember = isPrivateMember }

        fun chatRequestedById(chatRequestedById: String?) =
            apply { this.chatRequestedById = chatRequestedById }

        fun chatRequestCreatedAt(chatRequestCreatedAt: Long?) =
            apply { this.chatRequestCreatedAt = chatRequestCreatedAt }

        fun chatRequestedBy(chatRequestedBy: MemberViewData?) =
            apply { this.chatRequestedBy = chatRequestedBy }

        fun chatroomWithUser(chatroomWithUser: MemberViewData?) =
            apply { this.chatroomWithUser = chatroomWithUser }

        fun chatroomWithUserId(chatroomWithUserId: String?) =
            apply { this.chatroomWithUserId = chatroomWithUserId }

        fun build() = ChatroomViewData(
            id,
            memberViewData,
            title,
            createdAt,
            alreadySeenFullConversation,
            answerText,
            shortAnswer,
            state,
            shareUrl,
            communityId,
            communityName,
            type,
            memberState,
            about,
            header,
            showFollowTelescope,
            showFollowAutoTag,
            cardCreationTime,
            participantsCount,
            totalResponseCount,
            totalAllResponseCount,
            muteStatus,
            followStatus,
            date,
            hideBottomLine,
            isTagged,
            isPending,
            isPinned,
            deletedBy,
            updatedAt,
            draftConversation,
            isSecret,
            isDisabled,
            secretChatroomParticipants,
            secretChatroomLeft,
            unseenCount,
            isEdited,
            topic,
            reactions,
            memberCanMessage,
            chatroomImageUrl,
            unreadConversationCount,
            autoFollowDone,
            dynamicViewType,
            deletedByMember,
            chatRequestState,
            isPrivateMember,
            chatRequestedById,
            chatRequestCreatedAt,
            chatRequestedBy,
            chatroomWithUser,
            chatroomWithUserId
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .memberViewData(memberViewData)
            .title(title)
            .createdAt(createdAt)
            .alreadySeenFullConversation(alreadySeenFullConversation)
            .answerText(answerText)
            .shortAnswer(shortAnswer)
            .state(state)
            .shareUrl(shareUrl)
            .communityId(communityId)
            .communityName(communityName)
            .type(type)
            .memberState(memberState)
            .about(about)
            .header(header)
            .showFollowTelescope(showFollowTelescope)
            .showFollowAutoTag(showFollowAutoTag)
            .cardCreationTime(cardCreationTime)
            .participantsCount(participantsCount)
            .totalResponseCount(totalResponseCount)
            .totalAllResponseCount(totalAllResponseCount)
            .muteStatus(muteStatus)
            .followStatus(followStatus)
            .date(date)
            .hideBottomLine(hideBottomLine)
            .isTagged(isTagged)
            .isPending(isPending)
            .isPinned(isPinned)
            .deletedBy(deletedBy)
            .updatedAt(updatedAt)
            .draftConversation(draftConversation)
            .isSecret(isSecret)
            .isDisabled(isDisabled)
            .secretChatroomParticipants(secretChatroomParticipants)
            .secretChatroomLeft(secretChatroomLeft)
            .unseenCount(unseenCount)
            .isEdited(isEdited)
            .topic(topic)
            .reactions(reactions)
            .memberCanMessage(memberCanMessage)
            .chatroomImageUrl(chatroomImageUrl)
            .unreadConversationCount(unreadConversationCount)
            .autoFollowDone(autoFollowDone)
            .dynamicViewType(dynamicViewType)
            .deletedByMember(deletedByMember)
            .chatRequestState(chatRequestState)
            .isPrivateMember(isPrivateMember)
            .chatRequestedById(chatRequestedById)
            .chatRequestCreatedAt(chatRequestCreatedAt)
            .chatRequestedBy(chatRequestedBy)
            .chatroomWithUser(chatroomWithUser)
            .chatroomWithUserId(chatroomWithUserId)
    }
}