package com.likeminds.chatmm.utils

import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.*
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.conversation.model.*
import com.likeminds.chatmm.media.model.SingleUriData
import com.likeminds.chatmm.member.model.MemberStateViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.polls.model.PollInfoData
import com.likeminds.chatmm.polls.model.PollViewData
import com.likeminds.chatmm.reactions.model.ReactionViewData
import com.likeminds.chatmm.search.model.*
import com.likeminds.chatmm.search.util.SearchUtils
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.model.ITEM_VIEW_PARTICIPANTS
import com.likeminds.likemindschat.chatroom.model.*
import com.likeminds.likemindschat.community.model.Member
import com.likeminds.likemindschat.conversation.model.*
import com.likeminds.likemindschat.helper.model.GroupTag
import com.likeminds.likemindschat.poll.model.Poll
import com.likeminds.likemindschat.search.model.SearchChatroom
import com.likeminds.likemindschat.search.model.SearchConversation
import com.likeminds.likemindschat.user.model.*

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
            .chatroomId(conversation.chatroomId)
            .communityId(conversation.communityId)
            .lastSeen(conversation.lastSeen)
            .replyConversation(convertConversation(conversation.replyConversation))
            .isEdited(conversation.isEdited)
            .localCreatedEpoch(conversation.localCreatedEpoch)
            .replyChatroomId(conversation.replyChatroomId)
            .attachmentCount(conversation.attachmentCount ?: 0)
            .attachmentsUploaded(conversation.attachmentUploaded)
            .uploadWorkerUUID(conversation.uploadWorkerUUID)
            .temporaryId(conversation.temporaryId)
            .shortAnswer(ViewMoreUtil.getShortAnswer(conversation.answer, 1000))
            .pollInfoData(convertPollInfoData(conversation))
            .lastSeen(conversation.lastSeen)
            .reactions(convertConversationReactions(conversation.reactions, conversation.id))
            .build()
    }

    // converts network [Member] to [MemberViewData]
    fun convertMember(member: Member?): MemberViewData {
        if (member == null) {
            return MemberViewData.Builder().build()
        }
        return MemberViewData.Builder()
            .id(member.id)
            .name(member.name)
            .imageUrl(member.imageUrl)
            .state(member.state ?: 0)
            .customIntroText(member.customIntroText)
            .customClickText(member.customClickText)
            .customTitle(member.customTitle)
            .communityId(member.communityId.toString())
            .isOwner(member.isOwner)
            .isGuest(member.isGuest)
            .build()
    }

    // converts list of conversation reactions to list of [ReactionViewData]
    private fun convertConversationReactions(
        reactions: List<Reaction>?,
        conversationId: String?
    ): List<ReactionViewData>? {
        return reactions?.map {
            convertConversationReaction(it, conversationId)
        }
    }

    // converts a [Reaction] to [ReactionViewData]
    private fun convertConversationReaction(
        reactionData: Reaction,
        conversationId: String?
    ): ReactionViewData {
        return ReactionViewData.Builder()
            .reaction(reactionData.reaction)
            .conversationId(conversationId)
            .build()
    }

    // converts network model [MemberStateResponse] to [MemberStateViewData]
    fun convertMemberState(memberStateResponse: MemberStateResponse?): MemberStateViewData? {
        if (memberStateResponse == null) {
            return null
        }
        return MemberStateViewData.Builder()
            .state(memberStateResponse.state)
            .memberViewData(convertMemberFromMemberState(memberStateResponse))
            .managerRights(memberStateResponse.managerRights?.mapNotNull {
                convertManagementRights(it)
            })
            .memberRights(memberStateResponse.memberRights.mapNotNull {
                convertManagementRights(it)
            })
            .build()
    }

    // todo: remove
    private fun convertMemberFromMemberState(
        memberStateResponse: MemberStateResponse?
    ): MemberViewData? {
        if (memberStateResponse == null) {
            return null
        }

        return MemberViewData.Builder()
            .id(memberStateResponse.id)
            .state(memberStateResponse.state)
            .userUniqueId(memberStateResponse.userUniqueId)
            .customTitle(memberStateResponse.customTitle)
            .imageUrl(memberStateResponse.imageUrl)
            .isGuest(memberStateResponse.isGuest)
            .isOwner(memberStateResponse.isOwner)
            .name(memberStateResponse.name)
            .updatedAt(memberStateResponse.updatedAt)
            .build()
    }

    // converts network model [ManagementRightPermissionData] to [ManagementRightPermissionViewData]
    fun convertManagementRights(
        managementRightPermissionData: ManagementRightPermissionData?
    ): ManagementRightPermissionViewData? {
        if (managementRightPermissionData == null) {
            return null
        }
        return ManagementRightPermissionViewData.Builder()
            .id(managementRightPermissionData.id)
            .state(managementRightPermissionData.state)
            .title(managementRightPermissionData.title)
            .subtitle(managementRightPermissionData.subtitle)
            .isSelected(managementRightPermissionData.isSelected)
            .isLocked(managementRightPermissionData.isLocked)
            .build()
    }

    // converts poll data from network conversation to [PollInfoData]
    private fun convertPollInfoData(conversation: Conversation): PollInfoData {
        return PollInfoData.Builder()
            .allowAddOption(conversation.allowAddOption)
            .pollType(conversation.pollType)
            .pollViewDataList(convertPolls(conversation.polls))
            .expiryTime(conversation.expiryTime)
            .isAnonymous(conversation.isAnonymous)
            .multipleSelectNum(conversation.multipleSelectNum)
            .pollTypeText(conversation.pollTypeText)
            .submitTypeText(conversation.submitTypeText)
            .multipleSelectState(conversation.multipleSelectState)
            .pollAnswerText(conversation.pollAnswerText)
            .toShowResult(conversation.toShowResults)
            .isPollSubmitted(checkIsPollSubmitted(conversation.polls?.toMutableList()))
            .build()
    }

    // checks if poll is submitted or not and sets poll isSelected key
    private fun checkIsPollSubmitted(polls: MutableList<Poll>?): Boolean {
        var isPollSubmitted = false
        polls?.forEach {
            if (it.isSelected == true) {
                isPollSubmitted = true
            }
        }
        return isPollSubmitted
    }

    // converts network list of [Poll] to list of [PollViewData]
    private fun convertPolls(polls: List<Poll>?): List<PollViewData>? {
        return polls?.map {
            convertPoll(it)
        }
    }

    // converts network [Poll] to [PollViewData]
    fun convertPoll(poll: Poll): PollViewData {
        return PollViewData.Builder()
            .id(poll.id)
            .member(convertMember(poll.member))
            .isSelected(poll.isSelected)
            .text(poll.text)
            .percentage(poll.percentage)
            .noVotes(poll.noVotes)
            .isSelected(poll.isSelected)
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

    fun convertUserTag(member: Member?): TagViewData? {
        if (member == null) return null
        val nameDrawable = MemberImageUtil.getNameDrawable(
            MemberImageUtil.SIXTY_PX,
            member.id,
            member.name
        )
        return TagViewData.Builder()
            .name(member.name)
            // todo:
            .id(member.id.toInt())
            .imageUrl(member.imageUrl)
            .isGuest(member.isGuest)
            .userUniqueId(member.userUniqueId)
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
            .isEdited(conversationViewData.isEdited)
            .state(conversationViewData.state)
            .replyConversationId(conversationViewData.replyConversation?.id)
            .replyChatroomId(conversationViewData.replyChatroomId)
            .attachmentCount(conversationViewData.attachmentCount)
            .attachmentUploaded(conversationViewData.attachmentsUploaded)
            .uploadWorkerUUID(conversationViewData.uploadWorkerUUID)
            .localCreatedEpoch(conversationViewData.localCreatedEpoch)
            .deletedBy(conversationViewData.deletedBy)
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

    // converts PollViewData model list to network Poll model list
    fun createPolls(pollViewDataList: List<PollViewData>): List<Poll> {
        return pollViewDataList.map {
            convertPoll(it)
        }
    }

    // converts PollViewData model to network Poll model
    fun convertPoll(pollViewData: PollViewData): Poll {
        return Poll.Builder()
            .id(pollViewData.id)
            .isSelected(pollViewData.isSelected)
            .noVotes(pollViewData.noVotes)
            .percentage(pollViewData.percentage)
            .text(pollViewData.text)
            .build()
    }

    fun convertSearchChatroomHeaders(
        chatrooms: List<SearchChatroom>,
        followStatus: Boolean,
        keyword: String
    ): List<SearchChatroomHeaderViewData> {
        return chatrooms.map {
            convertSearchChatroomHeader(it, followStatus, keyword)
        }
    }

    private fun convertSearchChatroomHeader(
        searchedChatroom: SearchChatroom,
        followStatus: Boolean,
        keyword: String
    ): SearchChatroomHeaderViewData {
        return SearchChatroomHeaderViewData.Builder()
            .chatroom(convertChatroomForSearch(searchedChatroom))
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchedChatroom.community.name
                )
            )
            .build()
    }

    fun convertSearchChatroomTitles(
        chatrooms: List<SearchChatroom>,
        followStatus: Boolean,
        keyword: String
    ): List<SearchChatroomTitleViewData> {
        return chatrooms.map {
            convertSearchChatroomTitle(it, followStatus, keyword)
        }
    }

    // todo: community
    private fun convertSearchChatroomTitle(
        searchChatroom: SearchChatroom,
        followStatus: Boolean,
        keyword: String
    ): SearchChatroomTitleViewData {
        return SearchChatroomTitleViewData.Builder()
            .chatroom(convertChatroomForSearch(searchChatroom))
//            .community(convertCommunityForSearch(searchChatroom.community))
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchChatroom.community.name
                )
            )
            .keywordMatchedInChatroomName(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchChatroom.chatroom.header
                )
            )
            .keywordMatchedInMessageText(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchChatroom.chatroom.title,
                    isMessage = true
                )
            )
            .build()
    }

    private fun convertChatroomForSearch(
        searchChatroom: SearchChatroom
    ): ChatroomViewData {
        val member = MemberViewData.Builder()
            .id(searchChatroom.member.id)
            .name(searchChatroom.member.name)
            .build()

        return ChatroomViewData.Builder()
            .id(searchChatroom.chatroom.id)
            .communityId(searchChatroom.chatroom.communityId ?: "")
            .communityName(searchChatroom.chatroom.communityName ?: "")
            .memberViewData(member)
            .dynamicViewType(0)
            .createdAt(searchChatroom.chatroom.createdAt ?: 0L)
            .title(searchChatroom.chatroom.title)
            .answerText(searchChatroom.chatroom.answerText)
            .state(searchChatroom.state)
            .type(searchChatroom.chatroom.type)
            .header(searchChatroom.chatroom.header)
            .muteStatus(searchChatroom.muteStatus)
            .followStatus(searchChatroom.followStatus)
            .date(searchChatroom.chatroom.date)
            .isTagged(searchChatroom.isTagged)
            .isPending(searchChatroom.chatroom.isPending)
            .deletedBy(searchChatroom.chatroom.deletedBy)
            .updatedAt(searchChatroom.updatedAt)
            .isSecret(searchChatroom.chatroom.isSecret)
            .isDisabled(searchChatroom.isDisabled)
            .chatroomImageUrl(searchChatroom.chatroom.chatroomImageUrl)
            .build()
    }

    fun convertSearchConversations(
        conversations: List<SearchConversation>,
        followStatus: Boolean,
        keyword: String
    ): List<SearchConversationViewData> {
        return conversations.map {
            convertSearchConversation(it, followStatus, keyword)
        }
    }

    // todo: community
    private fun convertSearchConversation(
        searchConversation: SearchConversation,
        followStatus: Boolean,
        keyword: String
    ): SearchConversationViewData {
        return SearchConversationViewData.Builder()
            .chatroom(convertChatroom(searchConversation.chatroom))
//            .community(convertCommunityForSearch(searchConversation.community))
            .chatroomAnswer(convertConversationForSearch(searchConversation))
            .chatroomName(searchConversation.chatroom.header)
            .senderName(searchConversation.member.name)
            .chatroomAnswerId(searchConversation.id.toString())
            .answer(searchConversation.answer)
            .time(TimeUtil.getLastConversationTime(searchConversation.lastUpdated))
            .followStatus(followStatus)
            .keywordMatchedInCommunityName(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchConversation.community.name
                )
            )
            .keywordMatchedInChatroomName(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchConversation.chatroom.header
                )
            )
            .keywordMatchedInMessageText(
                SearchUtils.findMatchedKeywords(
                    keyword,
                    searchConversation.answer,
                    isMessage = true
                )
            )
            .build()
    }

    // todo: createdAt
    private fun convertConversationForSearch(
        searchConversation: SearchConversation
    ): ConversationViewData {
        val member = MemberViewData.Builder()
            .id(searchConversation.member.id)
            .name(searchConversation.member.name)
            .build()

        return ConversationViewData.Builder()
            .id(searchConversation.id.toString())
            .state(searchConversation.state)
            .attachmentCount(searchConversation.attachmentCount)
            .attachmentsUploaded(searchConversation.attachmentsUploaded)
            .isEdited(searchConversation.isEdited)
            .createdAt(searchConversation.createdAt.toString())
            .communityId(searchConversation.community.id)
            .memberViewData(member)
//            .createdAt(searchConversation.chatroom.createdAt)
            .answer(searchConversation.answer)
            .date(searchConversation.chatroom.date)
            .deletedBy(searchConversation.chatroom.deletedBy)
            .build()
    }

    // todo: uuid
    fun convertParticipants(participant: Member): MemberViewData {
        return MemberViewData.Builder()
            .dynamicViewType(ITEM_VIEW_PARTICIPANTS)
            .id(participant.id)
            .imageUrl(participant.imageUrl)
            .isGuest(participant.isGuest)
            .name(participant.name)
            .userUniqueId(participant.userUniqueId)
            .customTitle(participant.customTitle)
            .build()
    }
}