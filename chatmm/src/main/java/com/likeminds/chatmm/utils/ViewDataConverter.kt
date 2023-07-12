package com.likeminds.chatmm.utils

import android.net.Uri
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.conversation.model.AttachmentMetaViewData
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.LinkOGTagsViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.utils.model.ITEM_HOME_CHAT_ROOM
import com.likeminds.likemindschat.chatroom.model.Chatroom
import com.likeminds.likemindschat.community.model.Member
import com.likeminds.likemindschat.conversation.model.Attachment
import com.likeminds.likemindschat.conversation.model.Conversation
import com.likeminds.likemindschat.conversation.model.LinkOGTags
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

    private fun convertMember(member: Member?): MemberViewData {
        // todo: uid
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
}