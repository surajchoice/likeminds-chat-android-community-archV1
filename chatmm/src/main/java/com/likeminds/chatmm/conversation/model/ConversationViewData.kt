package com.likeminds.chatmm.conversation.model

import android.os.Parcelable
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.utils.model.*
import kotlinx.parcelize.Parcelize

@Parcelize
class ConversationViewData private constructor(
    val id: String,
    val memberViewData: MemberViewData?,
    val answer: String,
    val shortAnswer: String?,
    val alreadySeenFullConversation: Boolean?,
    val createdAt: String?,
    val chatroomId: String?,
    val communityId: String?,
    val state: Int,
    val attachments: List<AttachmentViewData>?,
    val attachmentUploadProgress: Pair<Long, Long>?,
    val lastSeen: Boolean?,
    val ogTags: LinkOGTagsViewData?,
    val date: String?,
    val replyConversation: ConversationViewData?,
    val isEdited: Boolean?,
    val deletedBy: String?,
    val attachmentCount: Int?,
    val attachmentsUploaded: Boolean?,
    val uploadWorkerUUID: String?,
    val temporaryId: String?,
    val createdEpoch: Long?,
    val localCreatedEpoch: Long?,
    val reactions: List<ReactionViewData>?,
    val replyChatroomId: String?,
    val isLastItem: Boolean?,
    val pollInfoData: PollInfoData?,
    val isExpanded: Boolean,
) : BaseViewType, Parcelable {
    override val viewType: Int
        get() = when (state) {
            STATE_HEADER,
            STATE_FOLLOWED,
            STATE_UN_FOLLOWED,
            STATE_EDIT_COMMUNITY_PURPOSE,
            STATE_GUEST_FOLLOWED,
            STATE_CHATROOM_ADD_PARTICIPANT,
            STATE_LEAVE_CHATROOM,
            STATE_REMOVED_FROM_CHATROOM,
            STATE_ADD_MEMBERS,
            STATE_TOPIC,
            DM_MEMBER_REMOVED_OR_LEFT,
            DM_CM_BECOMES_MEMBER_DISABLE,
            DM_MEMBER_BECOMES_CM,
            DM_CM_BECOMES_MEMBER_ENABLE,
            DM_MEMBER_BECOMES_CM_ENABLE -> {
                ITEM_CONVERSATION_ACTION
            }
            STATE_POLL -> {
                ITEM_CONVERSATION_POLL
            }
            else -> {
                if (ChatroomUtil.isUnsupportedConversation(this)) {
                    ITEM_CONVERSATION_UPDATE_APP
                }
                val imageCount: Int = ChatroomUtil.getMediaCount(IMAGE, attachments)
                val gifCount: Int = ChatroomUtil.getMediaCount(GIF, attachments)
                val pdfCount: Int = ChatroomUtil.getMediaCount(PDF, attachments)
                val videoCount: Int = ChatroomUtil.getMediaCount(VIDEO, attachments)
                val audioCount: Int = ChatroomUtil.getMediaCount(AUDIO, attachments)
                val voiceNoteCount: Int = ChatroomUtil.getMediaCount(VOICE_NOTE, attachments)
                if (imageCount == 1 && gifCount == 0 && pdfCount == 0 && videoCount == 0 && audioCount == 0 && voiceNoteCount == 0) ITEM_CONVERSATION_SINGLE_IMAGE
                if (imageCount == 0 && gifCount == 1 && pdfCount == 0 && videoCount == 0 && audioCount == 0 && voiceNoteCount == 0) ITEM_CONVERSATION_SINGLE_GIF
                if (imageCount == 0 && gifCount == 0 && pdfCount == 1 && videoCount == 0 && audioCount == 0 && voiceNoteCount == 0) ITEM_CONVERSATION_SINGLE_PDF
                if (imageCount == 0 && gifCount == 0 && pdfCount == 0 && videoCount == 1 && audioCount == 0 && voiceNoteCount == 0) ITEM_CONVERSATION_SINGLE_VIDEO
                if (imageCount == 0 && gifCount == 0 && pdfCount > 0 && videoCount == 0 && audioCount == 0 && voiceNoteCount == 0) {
                    ITEM_CONVERSATION_MULTIPLE_DOCUMENT
                }
                if (imageCount == 0 && gifCount == 0 && pdfCount == 0 && videoCount == 0 && audioCount > 0 && voiceNoteCount == 0) {
                    ITEM_CONVERSATION_AUDIO
                }
                if (imageCount == 0 && gifCount == 0 && pdfCount == 0 && videoCount == 0 && audioCount == 0 && voiceNoteCount > 0) {
                    ITEM_CONVERSATION_VOICE_NOTE
                }
                if (imageCount + gifCount + pdfCount + videoCount > 1) {
                    ITEM_CONVERSATION_MULTIPLE_MEDIA
                }
                if (ogTags != null) {
                    ITEM_CONVERSATION_LINK
                } else {
                    ITEM_CONVERSATION
                }
            }
        }

    fun isDeleted(): Boolean {
        return deletedBy != null
    }

    fun isNotDeleted(): Boolean {
        return deletedBy == null
    }

    fun isFailed(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        if (!isTemporaryConversation()) {
            return false
        }
        return if (localCreatedEpoch != null) {
            currentTimeMillis > localCreatedEpoch + 30 * 1000
        } else false
    }

    fun isTemporaryConversation(): Boolean {
        return id.startsWith("-")
    }

    fun isSending(): Boolean {
        if (isTemporaryConversation()) {
            return true
        }
        return if (attachmentCount != null && attachmentCount > 0) {
            attachmentsUploaded == null || !attachmentsUploaded
        } else {
            false
        }
    }

    fun isSent(): Boolean {
        if (isTemporaryConversation()) {
            return false
        }
        return if (attachmentCount != null && attachmentCount > 0) {
            attachmentsUploaded != null && attachmentsUploaded
        } else {
            true
        }
    }

    fun hasAnswer(): Boolean {
        return answer.isNotEmpty()
    }

    class Builder {
        private var id: String = ""
        private var memberViewData: MemberViewData? = null
        private var answer: String = ""
        private var shortAnswer: String? = null
        private var alreadySeenFullConversation: Boolean? = null
        private var createdAt: String? = null
        private var chatroomId: String? = null
        private var communityId: String? = null
        private var state: Int = 0
        private var attachments: List<AttachmentViewData>? = null
        private var attachmentUploadProgress: Pair<Long, Long>? = null
        private var lastSeen: Boolean? = null
        private var ogTags: LinkOGTagsViewData? = null
        private var date: String? = null
        private var replyConversation: ConversationViewData? = null
        private var isEdited: Boolean? = null
        private var deletedBy: String? = null
        private var attachmentCount: Int? = null
        private var attachmentsUploaded: Boolean? = null
        private var uploadWorkerUUID: String? = null
        private var temporaryId: String? = null
        private var createdEpoch: Long? = null
        private var localCreatedEpoch: Long? = null
        private var reactions: List<ReactionViewData>? = null
        private var replyChatroomId: String? = null
        private var isLastItem: Boolean? = null
        private var pollInfoData: PollInfoData? = null
        private var isExpanded: Boolean = false

        fun id(id: String) = apply { this.id = id }
        fun memberViewData(memberViewData: MemberViewData?) =
            apply { this.memberViewData = memberViewData }

        fun answer(answer: String) = apply { this.answer = answer }
        fun shortAnswer(shortAnswer: String?) = apply { this.shortAnswer = shortAnswer }
        fun alreadySeenFullConversation(alreadySeenFullConversation: Boolean?) =
            apply { this.alreadySeenFullConversation = alreadySeenFullConversation }

        fun createdAt(createdAt: String?) = apply { this.createdAt = createdAt }
        fun chatroomId(chatroomId: String?) = apply { this.chatroomId = chatroomId }
        fun communityId(communityId: String?) = apply { this.communityId = communityId }
        fun state(state: Int) = apply { this.state = state }
        fun attachments(attachments: List<AttachmentViewData>?) =
            apply { this.attachments = attachments }

        fun attachmentUploadProgress(attachmentUploadProgress: Pair<Long, Long>?) =
            apply { this.attachmentUploadProgress = attachmentUploadProgress }

        fun lastSeen(lastSeen: Boolean?) = apply { this.lastSeen = lastSeen }
        fun ogTags(ogTags: LinkOGTagsViewData?) = apply { this.ogTags = ogTags }
        fun date(date: String?) = apply { this.date = date }
        fun replyConversation(replyConversation: ConversationViewData?) =
            apply { this.replyConversation = replyConversation }

        fun isEdited(isEdited: Boolean?) = apply { this.isEdited = isEdited }
        fun deletedBy(deletedBy: String?) = apply { this.deletedBy = deletedBy }
        fun attachmentCount(attachmentCount: Int?) =
            apply { this.attachmentCount = attachmentCount }

        fun attachmentsUploaded(attachmentsUploaded: Boolean?) =
            apply { this.attachmentsUploaded = attachmentsUploaded }

        fun uploadWorkerUUID(uploadWorkerUUID: String?) =
            apply { this.uploadWorkerUUID = uploadWorkerUUID }

        fun temporaryId(temporaryId: String?) = apply { this.temporaryId = temporaryId }
        fun createdEpoch(createdEpoch: Long?) = apply { this.createdEpoch = createdEpoch }
        fun localCreatedEpoch(localCreatedEpoch: Long?) =
            apply { this.localCreatedEpoch = localCreatedEpoch }

        fun reactions(reactions: List<ReactionViewData>?) = apply { this.reactions = reactions }
        fun replyChatroomId(replyChatroomId: String?) =
            apply { this.replyChatroomId = replyChatroomId }

        fun isLastItem(isLastItem: Boolean?) = apply { this.isLastItem = isLastItem }
        fun pollInfoData(pollInfoData: PollInfoData?) = apply { this.pollInfoData = pollInfoData }
        fun isExpanded(isExpanded: Boolean) = apply { this.isExpanded = isExpanded }

        fun build() = ConversationViewData(
            id,
            memberViewData,
            answer,
            shortAnswer,
            alreadySeenFullConversation,
            createdAt,
            chatroomId,
            communityId,
            state,
            attachments,
            attachmentUploadProgress,
            lastSeen,
            ogTags,
            date,
            replyConversation,
            isEdited,
            deletedBy,
            attachmentCount,
            attachmentsUploaded,
            uploadWorkerUUID,
            temporaryId,
            createdEpoch,
            localCreatedEpoch,
            reactions,
            replyChatroomId,
            isLastItem,
            pollInfoData,
            isExpanded,
        )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .memberViewData(memberViewData)
            .answer(answer)
            .shortAnswer(shortAnswer)
            .alreadySeenFullConversation(alreadySeenFullConversation)
            .createdAt(createdAt)
            .chatroomId(chatroomId)
            .communityId(communityId)
            .state(state)
            .attachments(attachments)
            .attachmentUploadProgress(attachmentUploadProgress)
            .lastSeen(lastSeen)
            .ogTags(ogTags)
            .date(date)
            .replyConversation(replyConversation)
            .isEdited(isEdited)
            .deletedBy(deletedBy)
            .attachmentCount(attachmentCount)
            .attachmentsUploaded(attachmentsUploaded)
            .uploadWorkerUUID(uploadWorkerUUID)
            .temporaryId(temporaryId)
            .createdEpoch(createdEpoch)
            .localCreatedEpoch(localCreatedEpoch)
            .reactions(reactions)
            .replyChatroomId(replyChatroomId)
            .isLastItem(isLastItem)
            .pollInfoData(pollInfoData)
            .isExpanded(isExpanded)
    }
}