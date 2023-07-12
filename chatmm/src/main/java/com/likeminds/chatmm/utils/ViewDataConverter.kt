package com.likeminds.chatmm.utils

import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.ChatroomActionViewData
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.conversation.model.*
import com.likeminds.chatmm.media.model.SingleUriData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.chatroom.model.ChatroomAction
import com.likeminds.likemindschat.community.model.Member
import com.likeminds.likemindschat.conversation.model.*
import com.likeminds.likemindschat.helper.model.GroupTag
import com.likeminds.likemindschat.helper.model.UserTag
import com.likeminds.likemindschat.user.model.User

object ViewDataConverter {

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    fun convertChatroom(
        chatroom: Chatroom?,
        currentMemberId: String? = null,
        viewType: Int = 0,
    ): ChatroomViewData? {
        if (chatroom == null) {
            return null
        }

        var showFollowTelescope = false
        var showFollowAutoTag = false
        if (chatroom.followStatus == false) {
            showFollowTelescope = true
        }
        if (chatroom.member?.id == currentMemberId) {
            showFollowTelescope = false
        }
        if (chatroom.isTagged == true) {
            showFollowTelescope = false
            showFollowAutoTag = true
        }
        if (chatroom.followStatus == true) {
            showFollowTelescope = false
            showFollowAutoTag = false
        }

        return ChatroomViewData.Builder()
            .id(chatroom.id)
            .communityId(chatroom.communityId.toString())
            .communityName(chatroom.communityName ?: "")
            .memberViewData(convertMember(chatroom.member))
            .createdAt(chatroom.createdAt ?: 0)
            .title(chatroom.title)
            .answerText(chatroom.answerText)
            .state(chatroom.state)
            .shareUrl(chatroom.shareUrl)
            .type(chatroom.type)
            .date(chatroom.date)
            .about(chatroom.about)
            .header(chatroom.header)
            .showFollowTelescope(showFollowTelescope)
            .showFollowAutoTag(showFollowAutoTag)
            .cardCreationTime(chatroom.cardCreationTime)
            .totalResponseCount(chatroom.totalResponseCount)
            .totalAllResponseCount(chatroom.totalAllResponseCount ?: 0)
            .dynamicViewType(viewType)
            .muteStatus(chatroom.muteStatus ?: false)
            .followStatus(chatroom.followStatus ?: false)
            .hasBeenNamed(chatroom.hasBeenNamed)
            .date(chatroom.date)
            .isTagged(chatroom.isTagged)
            .isPending(chatroom.isPending)
            .deletedBy(chatroom.deletedBy)
            .updatedAt(chatroom.updatedAt)
            .draftConversation(chatroom.draftConversation)
            .isSecret(chatroom.isSecret)
            .secretChatroomParticipants(chatroom.secretChatroomParticipants?.toList())
            .secretChatroomLeft(chatroom.secretChatroomLeft)
            .unseenCount(chatroom.unseenCount)
            .isEdited(chatroom.isEdited)
            .autoFollowDone(chatroom.autoFollowDone)
            .topic(convertConversation(chatroom.topic))
            .reactions(chatroom.reactions?.mapNotNull { reaction ->
                convertChatroomReactions(reaction, chatroom.id)
            })
            .access(chatroom.access)
            .memberCanMessage(chatroom.memberCanMessage)
            .unreadConversationCount(chatroom.unreadConversationCount)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .build()
    }

    private fun convertChatroomReactions(
        reactionRO: Reaction?,
        id: String,
    ): ReactionViewData? {
        if (reactionRO == null) {
            return null
        }
        val memberViewData = convertMember(reactionRO.member)
        return ReactionViewData.Builder()
            .reaction(reactionRO.reaction)
            .memberViewData(memberViewData)
            .chatroomId(id)
            .build()
    }

    fun convertChatroomForHome(
        chatroom: Chatroom,
        dynamicViewType: Int? = null
    ): ChatroomViewData {
        // todo: member state
        return ChatroomViewData.Builder()
            .id(chatroom.id)
            .communityId(chatroom.communityId ?: "")
            .communityName(chatroom.communityName ?: "")
            .memberViewData(convertMember(chatroom.member))
            .createdAt(chatroom.createdAt ?: 0)
            .title(chatroom.title)
            .answerText(chatroom.answerText)
            .state(chatroom.state)
            .type(chatroom.type)
            .header(chatroom.header)
            .dynamicViewType(dynamicViewType)
            .muteStatus(chatroom.muteStatus ?: false)
            .followStatus(chatroom.followStatus ?: false)
            .date(chatroom.date)
            .isTagged(chatroom.isTagged)
            .isPending(chatroom.isPending)
            .deletedBy(chatroom.deletedBy)
            .updatedAt(chatroom.updatedAt)
            .isSecret(chatroom.isSecret)
            .unseenCount(chatroom.unseenCount)
            .isEdited(chatroom.isEdited)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .build()
    }

    // todo: member to uuid
    fun convertChatroomForExplore(
        chatroom: Chatroom?,
        memberId: String,
        sortIndex: Int
    ): ExploreViewData? {
        if (chatroom == null) return null
        return ExploreViewData.Builder()
            .isPinned(chatroom.isPinned ?: false)
            .isCreator(chatroom.member?.id == memberId)
            .externalSeen(chatroom.externalSeen)
            .isSecret(chatroom.isSecret ?: false)
            .followStatus(chatroom.followStatus ?: false)
            .participantsCount(chatroom.participantsCount?.toInt() ?: 0)
            .totalResponseCount(chatroom.totalResponseCount)
            .sortIndex(sortIndex)
            .id(chatroom.id)
            .header(chatroom.header ?: "")
            .title(chatroom.title)
            .imageUrl(chatroom.member?.imageUrl)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .chatroomViewData(convertChatroom(chatroom))
            .build()
    }

    /**
     * convert [Conversation] to [ConversationViewData]
     */
    fun convertConversations(conversations: List<Conversation>): List<ConversationViewData> {
        return conversations.mapNotNull {
            convertConversation(it)
        }
    }

    /**
     * convert [Conversation] to [ConversationViewData]
     */
    fun convertConversation(
        conversation: Conversation?,
        memberViewData: MemberViewData? = null
    ): ConversationViewData? {
        if (conversation == null) {
            return null
        }
        return ConversationViewData.Builder()
            .id(conversation.id ?: "")
            .memberViewData(memberViewData ?: convertMember(conversation.member))
            .createdAt(conversation.createdAt.toString())
            .createdEpoch(conversation.createdEpoch ?: 0L)
            .answer(conversation.answer)
            .state(conversation.state)
            .attachments(
                conversation.attachments?.mapNotNull { attachment ->
                    convertAttachment(attachment)
                }?.let {
                    ArrayList(it)
                }
            )
            .ogTags(convertOGTags(conversation.ogTags))
            .date(conversation.date)
            .deletedBy(conversation.deletedBy)
            .attachmentCount(conversation.attachmentCount ?: 0)
            .attachmentsUploaded(conversation.attachmentUploaded)
            .uploadWorkerUUID(conversation.uploadWorkerUUID)
            .temporaryId(conversation.temporaryId)
            .shortAnswer(ViewMoreUtil.getShortAnswer(conversation.answer, 1000))
            .build()
    }

    fun convertMember(member: Member?): MemberViewData {
        if (member == null) {
            return MemberViewData.Builder().build()
        }
        return MemberViewData.Builder()
            .id(member.id)
            .name(member.name)
            .imageUrl(member.imageUrl)
            .state(member.state ?: 0)
            .removeState(member.removeState)
            .customIntroText(member.customIntroText)
            .customClickText(member.customClickText)
            .customTitle(member.customTitle)
            .communityId(member.communityId.toString())
            .isOwner(member.isOwner)
            .isGuest(member.isGuest)
            .build()
    }

    // converts LinkOGTags view data model to network model
    private fun convertOGTags(
        linkOGTags: LinkOGTags?
    ): LinkOGTagsViewData? {
        if (linkOGTags == null) {
            return null
        }
        return LinkOGTagsViewData.Builder()
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .description(linkOGTags.description)
            .url(linkOGTags.url)
            .build()
    }

    private fun convertAttachment(
        attachment: Attachment?,
        title: String? = null,
        subTitle: String? = null
    ): AttachmentViewData? {
        if (attachment == null) {
            return null
        }
        val attachmentMeta = if (attachment.meta != null) {
            AttachmentMetaViewData.Builder()
                .duration(attachment.meta?.duration)
                .numberOfPage(attachment.meta?.numberOfPage)
                .size(attachment.meta?.size)
                .build()
        } else {
            null
        }
        return AttachmentViewData.Builder()
            .id(attachment.id)
            .name(attachment.name)
            .uri(Uri.parse(attachment.url))
            .type(attachment.type)
            .index(attachment.index)
            .width(attachment.width)
            .height(attachment.height)
            .title(title)
            .subTitle(subTitle)
            .awsFolderPath(attachment.awsFolderPath)
            .localFilePath(attachment.localFilePath)
            .thumbnail(attachment.thumbnailUrl)
            .thumbnailAWSFolderPath(attachment.thumbnailAWSFolderPath)
            .thumbnailLocalFilePath(attachment.thumbnailLocalFilePath)
            .meta(attachmentMeta)
            .build()
    }

    // todo: have to be refactored
    fun convertUser(user: User?): MemberViewData? {
        if (user == null) {
            return null
        }
        return MemberViewData.Builder()
            .id(user.id)
            .name(user.name)
            .imageUrl(user.imageUrl)
            .customTitle(user.customTitle)
            .isGuest(user.isGuest)
            .build()
    }

    fun convertGroupTag(groupTag: GroupTag?): TagViewData? {
        if (groupTag == null) return null
        return TagViewData.Builder()
            .name(groupTag.name)
            .imageUrl(groupTag.imageUrl)
            .tag(groupTag.tag)
            .route(groupTag.route)
            .description(groupTag.description)
            .build()
    }

    fun convertUserTag(userTag: UserTag?): TagViewData? {
        if (userTag == null) return null
        val nameDrawable = MemberImageUtil.getNameDrawable(
            MemberImageUtil.SIXTY_PX,
            userTag.id.toString(),
            userTag.name
        )
        return TagViewData.Builder()
            .name(userTag.name)
            .id(userTag.id)
            .imageUrl(userTag.imageUrl)
            .isGuest(userTag.isGuest)
            .userUniqueId(userTag.userUniqueId)
            .placeHolder(nameDrawable.first)
            .build()
    }

    /**
     * convert [LinkOGTags] to [LinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTags]
     **/
    fun convertLinkOGTags(linkOGTags: LinkOGTags): LinkOGTagsViewData {
        return LinkOGTagsViewData.Builder()
            .url(linkOGTags.url)
            .description(linkOGTags.description)
            .title(linkOGTags.title)
            .image(linkOGTags.image)
            .build()
    }

    /**
     * convert [LinkOGTags] to [LinkOGTagsViewData]
     * @param linkOGTags: object of [LinkOGTags]
     **/
    fun convertChatroomActions(chatroomActions: List<ChatroomAction>): List<ChatroomActionViewData> {
        return chatroomActions.map {
            convertChatroomAction(it)
        }
    }

    private fun convertChatroomAction(chatroomAction: ChatroomAction): ChatroomActionViewData {
        return ChatroomActionViewData.Builder()
            .id(chatroomAction.id.toString())
            .title(chatroomAction.title)
            .route(chatroomAction.route)
            .build()
    }

    /**--------------------------------
     * View Data Model -> Network Model
    --------------------------------*/

    // creates a Conversation network model for posting a conversation
    fun convertConversation(
        memberId: String,
        communityId: String?,
        request: PostConversationRequest,
        fileUris: List<SingleUriData>?
    ): Conversation {
        return Conversation.Builder()
            .id(request.temporaryId)
            .chatroomId(request.chatroomId)
            .communityId(communityId)
            .answer(request.text)
            .state(STATE_NORMAL)
            .createdEpoch(System.currentTimeMillis())
            .memberId(memberId)
            .createdAt(TimeUtil.generateCreatedAt())
            .attachments(convertAttachments(fileUris))
            .lastSeen(true)
            .ogTags(request.ogTags)
            .date(TimeUtil.generateDate())
            .replyConversationId(request.repliedConversationId)
            .attachmentCount(request.attachmentCount ?: 0)
            .localCreatedEpoch(System.currentTimeMillis())
            .temporaryId(request.temporaryId)
            .isEdited(false)
            .replyChatroomId(request.repliedChatroomId)
            .attachmentUploaded(false)
            .build()
    }

    // converts list of SingleUriData to list of network Attachment model
    private fun convertAttachments(fileUris: List<SingleUriData>?): List<Attachment>? {
        return fileUris?.mapIndexed { index, singleUriData ->
            convertAttachment(singleUriData, index)
        }
    }

    // converts SingleUriData to network Attachment model
    private fun convertAttachment(
        singleUriData: SingleUriData,
        index: Int
    ): Attachment {
        return Attachment.Builder()
            .name(singleUriData.mediaName)
            .url(singleUriData.uri.toString())
            .type(singleUriData.fileType)
            .index(index)
            .width(singleUriData.width)
            .height(singleUriData.height)
            .localFilePath(singleUriData.uri.toString())
            .thumbnailUrl(singleUriData.thumbnailUri.toString())
            .thumbnailLocalFilePath(singleUriData.thumbnailUri.toString())
            .meta(
                AttachmentMeta.Builder()
                    .numberOfPage(singleUriData.pdfPageCount)
                    .duration(singleUriData.duration)
                    .size(singleUriData.size)
                    .build()
            )
            .build()
    }

    // converts [ConversationViewData] to [Conversation] model
    fun convertConversation(
        conversationViewData: ConversationViewData
    ): Conversation {
        return Conversation.Builder()
            .id(conversationViewData.id)
            .chatroomId(conversationViewData.chatroomId)
            .communityId(conversationViewData.communityId)
            .answer(conversationViewData.answer)
            .createdEpoch(conversationViewData.createdEpoch)
            .memberId(conversationViewData.memberViewData.id)
            .createdAt(conversationViewData.createdAt)
            .attachments(convertAttachmentViewDataList(conversationViewData.attachments))
            .lastSeen(conversationViewData.lastSeen)
            .ogTags(convertLinkOGTags(conversationViewData.ogTags))
            .date(conversationViewData.date)
            .replyConversationId(conversationViewData.replyConversation?.id)
            .attachmentCount(conversationViewData.attachmentCount)
            .localCreatedEpoch(conversationViewData.localCreatedEpoch)
            .temporaryId(conversationViewData.temporaryId)
            .isEdited(conversationViewData.isEdited)
            .replyChatroomId(conversationViewData.replyChatroomId)
            .attachmentUploaded(conversationViewData.attachmentsUploaded)
            .build()
    }

    // converts list of AttachmentViewData to list of network Attachment model
    private fun convertAttachmentViewDataList(attachmentViewDataList: List<AttachmentViewData>?): List<Attachment>? {
        return attachmentViewDataList?.map { attachmentViewData ->
            convertAttachmentViewData(attachmentViewData)
        }
    }

    // converts AttachmentViewData to network Attachment model
    private fun convertAttachmentViewData(
        attachmentViewData: AttachmentViewData,
    ): Attachment {
        return Attachment.Builder()
            .name(attachmentViewData.name)
            .url(attachmentViewData.uri.toString())
            .type(attachmentViewData.type)
            .index(attachmentViewData.index)
            .width(attachmentViewData.width)
            .height(attachmentViewData.height)
            .localFilePath(attachmentViewData.uri.toString())
            .thumbnailUrl(attachmentViewData.thumbnail.toString())
            .thumbnailLocalFilePath(attachmentViewData.thumbnailLocalFilePath.toString())
            .meta(
                AttachmentMeta.Builder()
                    .numberOfPage(attachmentViewData.meta?.numberOfPage)
                    .duration(attachmentViewData.meta?.duration)
                    .size(attachmentViewData.meta?.size)
                    .build()
            )
            .build()
    }

    // converts LinkOGTags view data model to network model
    fun convertLinkOGTags(
        linkOGTagsViewData: LinkOGTagsViewData?
    ): LinkOGTags? {
        if (linkOGTagsViewData == null) {
            return null
        }
        return LinkOGTags.Builder()
            .title(linkOGTagsViewData.title)
            .image(linkOGTagsViewData.image)
            .description(linkOGTagsViewData.description)
            .url(linkOGTagsViewData.url)
            .build()
    }
}