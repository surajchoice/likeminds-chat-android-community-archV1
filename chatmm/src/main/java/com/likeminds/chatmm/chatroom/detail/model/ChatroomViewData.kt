package com.likeminds.chatmm.chatroom.detail.model

import android.os.Parcelable
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.PollInfoData
import com.likeminds.chatmm.conversation.model.ReactionViewData
import com.likeminds.chatmm.utils.model.BaseViewType
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import kotlinx.parcelize.Parcelize

@Parcelize
class ChatroomViewData private constructor(
    val id: String,
    val memberViewData: MemberViewData?,
    val title: String,
    val createdAt: Long?,
    val alreadySeenFullConversation: Boolean?,
    val answerText: String?,
    val shortAnswer: String?,
    val state: Int,
    val shareUrl: String?,
    val communityId: String?,
    val communityName: String?,
    val type: Int?,
    val memberState: Int?,
    val about: String?,
    val header: String?,
    val showFollowTelescope: Boolean?,
    val showFollowAutoTag: Boolean?,
    val cardCreationTime: String?,
    val participantsCount: Int?,
    val totalResponseCount: Int?,
    val totalAllResponseCount: Int?,
    val muteStatus: Boolean?,
    val followStatus: Boolean?,
    val hasBeenNamed: Boolean?,
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
    val pollInfoData: PollInfoData?,
    val isEdited: Boolean?,
    val topic: ConversationViewData?,
    val reactions: List<ReactionViewData>?,
    val access: Int?,
    val memberCanMessage: Boolean?,
    val chatroomImageUrl: String?,
    val unreadConversationCount: Int?,
    val autoFollowDone: Boolean?,
    val dynamicViewType: Int?,
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = dynamicViewType ?: ITEM_HOME_CHAT_ROOM

    fun answerTextUpdated(): String {
        return if (type != null && type == TYPE_UN_VERIFY) {
            "This user is not verified yet"
        } else if (answerText == null || answerText.isEmpty()) {
            "Be the first to respond"
        } else {
            answerText
        }
    }

    fun isDeleted(): Boolean {
        return deletedBy != null
    }

    fun hasTitle(): Boolean {
        return title.isNotEmpty()
    }

    class Builder {
        private var id: String = ""
        private var memberViewData: MemberViewData? = null
        private var title: String = ""
        private var createdAt: Long? = null
        private var alreadySeenFullConversation: Boolean? = null
        private var answerText: String? = null
        private var shortAnswer: String? = null
        private var state: Int = 0
        private var shareUrl: String? = null
        private var communityId: String? = null
        private var communityName: String? = null
        private var type: Int? = null
        private var memberState: Int? = null
        private var about: String? = null
        private var header: String? = null
        private var showFollowTelescope: Boolean? = null
        private var showFollowAutoTag: Boolean? = null
        private var cardCreationTime: String? = null
        private var participantsCount: Int? = null
        private var totalResponseCount: Int? = null
        private var totalAllResponseCount: Int? = null
        private var muteStatus: Boolean? = null
        private var followStatus: Boolean? = null
        private var hasBeenNamed: Boolean? = null
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
        private var pollInfoData: PollInfoData? = null
        private var isEdited: Boolean? = null
        private var topic: ConversationViewData? = null
        private var reactions: List<ReactionViewData>? = null
        private var access: Int? = null
        private var memberCanMessage: Boolean? = null
        private var chatroomImageUrl: String? = null
        private var unreadConversationCount: Int? = null
        private var autoFollowDone: Boolean? = null
        private var dynamicViewType: Int? = null

        fun id(id: String) = apply { this.id = id }
        fun memberViewData(memberViewData: MemberViewData?) =
            apply { this.memberViewData = memberViewData }

        fun title(title: String) = apply { this.title = title }
        fun createdAt(createdAt: Long?) = apply { this.createdAt = createdAt }
        fun alreadySeenFullConversation(alreadySeenFullConversation: Boolean?) =
            apply { this.alreadySeenFullConversation = alreadySeenFullConversation }

        fun answerText(answerText: String?) = apply { this.answerText = answerText }
        fun shortAnswer(shortAnswer: String?) = apply { this.shortAnswer = shortAnswer }
        fun state(state: Int) = apply { this.state = state }
        fun shareUrl(shareUrl: String?) = apply { this.shareUrl = shareUrl }
        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun communityName(communityName: String?) = apply { this.communityName = communityName }
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

        fun participantsCount(participantsCount: Int?) =
            apply { this.participantsCount = participantsCount }

        fun totalResponseCount(totalResponseCount: Int?) =
            apply { this.totalResponseCount = totalResponseCount }

        fun totalAllResponseCount(totalAllResponseCount: Int?) =
            apply { this.totalAllResponseCount = totalAllResponseCount }

        fun muteStatus(muteStatus: Boolean?) = apply { this.muteStatus = muteStatus }
        fun followStatus(followStatus: Boolean?) = apply { this.followStatus = followStatus }
        fun hasBeenNamed(hasBeenNamed: Boolean?) = apply { this.hasBeenNamed = hasBeenNamed }
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
        fun pollInfoData(pollInfoData: PollInfoData?) = apply { this.pollInfoData = pollInfoData }
        fun isEdited(isEdited: Boolean?) = apply { this.isEdited = isEdited }
        fun topic(topic: ConversationViewData?) = apply { this.topic = topic }
        fun reactions(reactions: List<ReactionViewData>?) = apply { this.reactions = reactions }
        fun access(access: Int?) = apply { this.access = access }
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
            hasBeenNamed,
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
            pollInfoData,
            isEdited,
            topic,
            reactions,
            access,
            memberCanMessage,
            chatroomImageUrl,
            unreadConversationCount,
            autoFollowDone,
            dynamicViewType
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
            .hasBeenNamed(hasBeenNamed)
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
            .pollInfoData(pollInfoData)
            .isEdited(isEdited)
            .topic(topic)
            .reactions(reactions)
            .access(access)
            .memberCanMessage(memberCanMessage)
            .chatroomImageUrl(chatroomImageUrl)
            .unreadConversationCount(unreadConversationCount)
            .autoFollowDone(autoFollowDone)
            .dynamicViewType(dynamicViewType)
    }
}