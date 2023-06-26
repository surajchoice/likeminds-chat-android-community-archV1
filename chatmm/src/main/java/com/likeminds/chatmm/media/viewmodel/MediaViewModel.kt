package com.likeminds.chatmm.media.viewmodel

import androidx.lifecycle.ViewModel
import com.likeminds.chatmm.media.MediaRepository
import javax.inject.Inject

class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    // todo:
    /**------------------------------------------------------------
     * Analytics events
    ---------------------------------------------------------------*/
    /***
     * Triggers when member shares a multimedia from outside the app
     */

    fun sendThirdPartySharingEvent(
        sharingType: String,
        chatroomType: String?,
        communityId: String,
        communityName: String?,
        searchKey: String?,
        chatroomId: String,
    ) {
        val search = searchKey ?: ""
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_THIRD_PARTY_SHARING,
//            "sharing_type" to sharingType,
//            "chatroom_type" to chatroomType,
//            "community_id" to communityId,
//            "community_name" to communityName,
//            "chatroom_id" to chatroomId,
//            "search_key" to search,
//            "new_chatroom_created" to "false"
//        )
    }

    /***
     * Triggers when member clicks on cross or back button
     */
    fun sendThirdPartyAbandoned(
        sharingType: String,
        communityId: String,
        communityName: String?,
        chatroomId: String,
    ) {
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_THIRD_PARTY_ABANDONED,
//            "sharing_type" to sharingType,
//            "community_id" to communityId,
//            "community_name" to communityName,
//            "chatroom_id" to chatroomId,
//        )
    }

    /**
     * Triggers when the user views a image message
     **/
    fun sendImageViewedEvent(chatroomId: String?, communityId: String?, messageId: String?) {
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_IMAGE_VIEWED,
//            "chatroom_id" to chatroomId,
//            "community_id" to communityId,
//            "message_id" to messageId
//        )
    }

    /**
     * Triggers when the user plays the video message
     **/
    fun sendVideoPlayedEvent(
        chatroomId: String?,
        communityId: String?,
        messageId: String?,
        type: String?
    ) {
//        LMAnalytics.track(
//            LMAnalytics.Keys.EVENT_VIDEO_PLAYED,
//            "chatroom_id" to chatroomId,
//            "community_id" to communityId,
//            "message_id" to messageId,
//            "type" to type
//        )
    }
}