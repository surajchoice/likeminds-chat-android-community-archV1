package com.likeminds.chatmm.chatroom.detail.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.content.ContextCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.chatroom.detail.model.TYPE_ANNOUNCEMENT
import com.likeminds.chatmm.chatroom.detail.model.TYPE_INTRO
import com.likeminds.chatmm.conversation.model.*
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.utils.ValueUtils.containsUrl

object ChatroomUtil {
    fun getMediaCount(mediaType: String, attachments: List<AttachmentViewData>?): Int {
        return attachments?.count { it.type == mediaType } ?: 0
    }

    fun isUnsupportedConversation(conversation: ConversationViewData): Boolean {
        // If conversation state is not defined on client side
        if (!ConversationsState.contains(conversation.state)) {
            return true
        }
        //If conversation state is normal and attachment type is not defined on client side
        if (
            conversation.state == STATE_NORMAL &&
            conversation.attachments?.firstOrNull {
                !InternalMediaType.contains(it.type)
            } != null
        ) {
            return true
        }
        return false
    }

    /**
     * @return a Triple : First -> SpannableStringBuilder | Second -> FirstMediaType | Third -> drawable list
     **/
    fun getHomeScreenAttachmentData(
        context: Context,
        conversation: ConversationViewData?
    ): Triple<SpannableStringBuilder, String, ArrayList<Pair<Int, Int>>> {
        val drawableList = ArrayList<Pair<Int, Int>>()
        val firstMediaType = getFirstMediaType(conversation?.attachments)

        val videoDrawable = getVideoDrawable(conversation)
        val otherDrawable = getAttachmentTypeDrawable(conversation)
        if (firstMediaType == VIDEO) {
            if (videoDrawable != null) drawableList.add(videoDrawable)
            if (otherDrawable != null) drawableList.add(otherDrawable)
        } else {
            if (otherDrawable != null) drawableList.add(otherDrawable)
            if (videoDrawable != null) drawableList.add(videoDrawable)
        }

        val spannableStringBuilder = SpannableStringBuilder()
        if (drawableList.size > 0) {
            drawableList.forEach { pair ->
                if (pair.second > 1 || drawableList.size > 1) {
                    val spannableString = SpannableString("${pair.second} #  ")
                    spannableString.setSpan(
                        ImageSpan(context, pair.first),
                        pair.second.toString().length + 1,
                        pair.second.toString().length + 2,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannableStringBuilder.append(spannableString)
                } else {
                    val spannableString = SpannableString("#  ")
                    spannableString.setSpan(
                        ImageSpan(context, pair.first),
                        0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    spannableStringBuilder.append(spannableString)
                }
            }
        }
        return Triple(spannableStringBuilder, firstMediaType, drawableList)
    }

    fun getFirstMediaType(attachmentList: List<AttachmentViewData>?): String {
        return when (val viewData = attachmentList?.minByOrNull { it.index ?: 0 }) {
            is AttachmentViewData -> viewData.type
            else -> ""
        }
    }

    private fun getVideoDrawable(conversation: ConversationViewData?): Pair<Int, Int>? {
        val videosCount = conversation?.attachments?.filter { it.type == VIDEO }?.size ?: 0
        return when {
            (videosCount > 0) -> Pair(R.drawable.ic_video_header, videosCount)
            else -> null
        }
    }

    private fun getAttachmentTypeDrawable(conversation: ConversationViewData?): Pair<Int, Int>? {
        val imagesCount = conversation?.attachments?.filter { it.type == IMAGE }?.size ?: 0
        val gifsCount = conversation?.attachments?.filter { it.type == GIF }?.size ?: 0
        val pdfsCount = conversation?.attachments?.filter { it.type == PDF }?.size ?: 0
        val audiosCount = conversation?.attachments?.filter { it.type == AUDIO }?.size ?: 0
        val voiceNoteCount =
            conversation?.attachments?.filter { it.type == VOICE_NOTE }?.size ?: 0
        return when {
            imagesCount > 0 -> {
                Pair(R.drawable.ic_photo_header, imagesCount)
            }
            gifsCount > 0 -> {
                Pair(R.drawable.ic_gif_header, gifsCount)
            }
            pdfsCount > 0 -> {
                Pair(R.drawable.ic_document_header, pdfsCount)
            }
            audiosCount > 0 -> {
                Pair(R.drawable.ic_audio_header_grey, audiosCount)
            }
            voiceNoteCount > 0 -> {
                Pair(R.drawable.ic_voice_note_header_grey, voiceNoteCount)
            }
            conversation?.ogTags != null || conversation?.answer
                .containsUrl() -> {
                Pair(R.drawable.ic_link_header, 1)
            }
            conversation?.state == STATE_POLL -> {
                Pair(R.drawable.ic_micro_poll, 1)
            }
            else -> null
        }
    }

    fun getDeletedMessage(
        context: Context,
        conversation: ConversationViewData,
        currentMemberId: String,
    ): String {
        return if (conversation.memberViewData?.userUniqueId == currentMemberId) {
            if (conversation.deletedBy == currentMemberId) {
                context.getString(R.string.you_deleted_this_message)
            } else {
                context.getString(R.string.your_message_was_deleted_by_cm)
            }
        } else {
            if (conversation.memberViewData?.id == conversation.deletedBy) {
                context.getString(R.string.this_message_was_deleted)
            } else {
                context.getString(R.string.this_message_was_deleted_by_cm)
            }
        }
    }

    fun getTypeDrawableId(type: Int?): Int? {
        return when (type) {
            TYPE_INTRO -> {
                R.drawable.ic_intro_room
            }
            TYPE_ANNOUNCEMENT -> {
                R.drawable.ic_announcement_room
            }
            else -> {
                return null
            }
        }
    }

    fun getTypeDrawable(context: Context, type: Int?): Drawable? {
        val drawableId = getTypeDrawableId(type)
        return getDrawable(context, drawableId)
    }

    fun getDrawable(context: Context, drawableId: Int?): Drawable? {
        return if (drawableId != null) ContextCompat.getDrawable(context, drawableId)
        else null
    }

    fun getLastConversationTextForHome(lastConversation: ConversationViewData?): String {
        var lastConversationAnswer = lastConversation?.answer
        //If it's just a link with no answer show the title of link
        if (lastConversationAnswer.isNullOrBlank()) {
            val linkTitle = lastConversation?.ogTags?.title
            if (!linkTitle.isNullOrBlank()) {
                lastConversationAnswer = linkTitle
            }
        }

        //If it's just a media with no answer
        if (lastConversationAnswer.isNullOrBlank()) {
            val imagesCount =
                lastConversation?.attachments?.filter { it.type == IMAGE }?.size ?: 0
            val gifsCount =
                lastConversation?.attachments?.filter { it.type == GIF }?.size ?: 0
            val videoCount =
                lastConversation?.attachments?.filter { it.type == VIDEO }?.size ?: 0
            val audioCount =
                lastConversation?.attachments?.filter { it.type == AUDIO }?.size ?: 0
            val pdfCount = lastConversation?.attachments?.filter { it.type == PDF }?.size ?: 0
            val voiceNoteCount =
                lastConversation?.attachments?.filter { it.type == VOICE_NOTE }?.size ?: 0
            val firstMediaType = getFirstMediaType(lastConversation?.attachments)
            when {
                imagesCount > 0 && videoCount > 0 -> {
                    lastConversationAnswer = ""
                }
                firstMediaType == IMAGE -> {
                    lastConversationAnswer = if (imagesCount > 1) "Photos" else "Photo"
                }
                firstMediaType == GIF -> {
                    lastConversationAnswer = if (gifsCount > 1) "Gifs" else "GIF"
                }
                firstMediaType == VIDEO -> {
                    lastConversationAnswer = if (videoCount > 1) "Videos" else "Video"
                }
                firstMediaType == PDF -> {
                    lastConversationAnswer = if (pdfCount > 1) "Documents" else "Document"
                }
                firstMediaType == AUDIO -> {
                    lastConversationAnswer = if (audioCount > 1) "Audios" else "Audio"
                }
                firstMediaType == VOICE_NOTE -> {
                    lastConversationAnswer = if (voiceNoteCount > 1) "Voice Notes" else "Voice Note"
                }
            }
        }
        return lastConversationAnswer ?: ""
    }
}