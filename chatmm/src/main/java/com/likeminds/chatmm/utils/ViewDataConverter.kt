package com.likeminds.chatmm.utils

import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.conversation.model.AttachmentMetaViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.LinkOGTagsViewData
import com.likeminds.chatmm.search.model.SearchChatroomHeaderViewData
import com.likeminds.chatmm.search.model.SearchChatroomTitleViewData
import com.likeminds.chatmm.search.model.SearchConversationViewData
import com.likeminds.chatmm.search.util.SearchUtils
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.community.model.Member
import com.likeminds.likemindschat.conversation.model.Attachment
import com.likeminds.likemindschat.conversation.model.Conversation
import com.likeminds.likemindschat.conversation.model.LinkOGTags
import com.likeminds.likemindschat.helper.model.GroupTag
import com.likeminds.likemindschat.helper.model.UserTag
import com.likeminds.likemindschat.search.model.SearchChatroom
import com.likeminds.likemindschat.search.model.SearchConversation
import com.likeminds.likemindschat.user.model.User

object ViewDataConverter {

    /**--------------------------------
     * Network Model -> View Data Model
    --------------------------------*/

    fun convertChatroom(chatroom: Chatroom): ChatroomViewData {
        // todo: member state
        return ChatroomViewData.Builder()
            .id(chatroom.id)
            .communityId(chatroom.communityId)
            .communityName(chatroom.communityName)
            .memberViewData(convertMember(chatroom.member))
//            .createdAt(chatroom.createdAt)
            .title(chatroom.title)
            .answerText(chatroom.answerText)
            .state(chatroom.state)
            .type(chatroom.type)
            .header(chatroom.header)
            .dynamicViewType(ITEM_HOME_CHAT_ROOM)
            .muteStatus(chatroom.muteStatus)
            .followStatus(chatroom.followStatus)
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
    fun convertChatroom(
        chatroom: Chatroom?,
        memberId: String,
        sortIndex: Int
    ): ExploreViewData? {
        if (chatroom == null) return null
        return ExploreViewData.Builder()
            .isPinned(chatroom.isPinned)
            .isCreator(chatroom.member?.id == memberId)
            .externalSeen(chatroom.externalSeen)
            .isSecret(chatroom.isSecret)
            .followStatus(chatroom.followStatus)
            .participantsCount(chatroom.participantsCount?.toIntOrNull())
            .totalResponseCount(chatroom.totalResponseCount)
            .sortIndex(sortIndex)
            .id(chatroom.id)
            .header(chatroom.header)
            .title(chatroom.title)
            .imageUrl(chatroom.member?.imageUrl)
            .chatroomImageUrl(chatroom.chatroomImageUrl)
            .chatroomViewData(convertChatroom(chatroom))
            .build()
    }

    /**
     * convert [Conversation] to [ConversationViewData]
     */
    fun convertConversation(conversation: Conversation?): ConversationViewData? {
        if (conversation == null) {
            return null
        }
        return ConversationViewData.Builder()
            .id(conversation.id ?: "")
            .memberViewData(convertMember(conversation.member))
            .createdAt(conversation.createdAt.toString())
            .answer(conversation.answer)
            .state(conversation.state)
            .attachments(conversation.attachments?.mapNotNull { attachment ->
                convertAttachment(attachment)
            })
            .ogTags(convertOGTags(conversation.ogTags))
            .date(conversation.date)
            .deletedBy(conversation.deletedBy)
            .attachmentCount(conversation.attachmentCount)
            .attachmentsUploaded(conversation.attachmentUploaded)
            .uploadWorkerUUID(conversation.uploadWorkerUUID)
            .shortAnswer(ViewMoreUtil.getShortAnswer(conversation.answer, 1000))
            .build()
    }

    private fun convertMember(member: Member?): MemberViewData? {
        // todo: uid
        if (member == null) {
            return null
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

    // todo: createdAt
    private fun convertChatroomForSearch(
        searchChatroom: SearchChatroom
    ): ChatroomViewData {
        val member = MemberViewData.Builder()
            .id(searchChatroom.member.id.toString())
            .name(searchChatroom.member.profile.name)
            .build()

        return ChatroomViewData.Builder()
            .id(searchChatroom.chatroom.id)
            .communityId(searchChatroom.chatroom.communityId)
            .communityName(searchChatroom.chatroom.communityName)
            .memberViewData(member)
            .dynamicViewType(0)
//            .createdAt(searchChatroom.chatroom.createdAt)
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
            .senderName(searchConversation.member.profile.name)
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
            .id(searchConversation.member.id.toString())
            .name(searchConversation.member.profile.name)
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
}