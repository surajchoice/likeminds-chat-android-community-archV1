package com.likeminds.chatmm.media.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giphy.sdk.core.models.Media
import com.likeminds.chatmm.media.MediaRepository
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.util.MediaUtils
import com.likeminds.chatmm.utils.GiphyUtil
import com.likeminds.chatmm.utils.coroutine.launchDefault
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.file.util.FileUtil
import com.likeminds.likemindschat.LMChatClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class MediaViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val lmChatClient = LMChatClient.getInstance()

    private val errorEventChannel = Channel<ErrorMessageEvent>(Channel.BUFFERED)
    val errorEventFlow = errorEventChannel.receiveAsFlow()

    private val _mediaListUri by lazy { MutableLiveData<List<SingleUriData>>() }
    val mediaListUri: LiveData<List<SingleUriData>> = _mediaListUri

    private val _audioByteArray by lazy { MutableLiveData<ByteArray>() }
    val audioByteArray: LiveData<ByteArray> = _audioByteArray

    private val _updatedUriDataList by lazy { MutableLiveData<List<SingleUriData>>() }
    val updatedUriDataList: LiveData<List<SingleUriData>> = _updatedUriDataList

    private val _documentPreviewLiveData by lazy { MutableLiveData<List<SingleUriData>>() }
    val documentPreviewLiveData: LiveData<List<SingleUriData>> = _documentPreviewLiveData

    private val _giphyMedia by lazy { MutableLiveData<Pair<Boolean, Uri?>>() }
    val giphyMedia: LiveData<Pair<Boolean, Uri?>> = _giphyMedia

    sealed class ErrorMessageEvent {
    }

    fun createThumbnailForAudio(
        context: Context,
        mediaUris: MutableList<SingleUriData>?,
    ) {
        viewModelScope.launchDefault {
            _mediaListUri.postValue(mediaRepository.createThumbnailForAudio(context, mediaUris))
        }
    }

    fun convertUriToByteArray(context: Context, uri: Uri) {
        viewModelScope.launchDefault {
            _audioByteArray.postValue(mediaRepository.convertUriToByteArray(context, uri))
        }
    }

    /**
     * Fetches document preview asynchronously
     */
    fun fetchDocumentPreview(
        context: Context, uris: List<SingleUriData>,
    ) = viewModelScope.launchDefault {
        val updatedUris = uris.filter { singleUriData ->
            singleUriData.thumbnailUri == null
        }.mapNotNull { singleUriData ->
            val uri = MediaUtils.getDocumentPreview(context, singleUriData.uri)
            if (uri != null) {
                singleUriData.toBuilder().thumbnailUri(uri).build()
            } else {
                null
            }
        }
        _documentPreviewLiveData.postValue(updatedUris)
    }

    fun fetchUriDetails(
        context: Context,
        uris: List<Uri>,
        callback: (media: List<MediaViewData>) -> Unit,
    ) {
        mediaRepository.getLocalUrisDetails(context, uris, callback)
    }

    fun getGiphyUri(context: Context, media: Media) {
        _giphyMedia.postValue(Pair(true, null))
        GiphyUtil.getGifLink(media)?.let { link ->
            viewModelScope.launchIO {
                FileUtil.getGifUri(context, link)?.let { uri ->
                    _giphyMedia.postValue(Pair(false, uri))
                }
            }
        }
    }

    fun fetchExternallySharedUriData(context: Context, uris: List<Uri>) =
        viewModelScope.launchDefault {
            val dataList = uris.mapNotNull { uri ->
                val singleUriData = mediaRepository.getExternallySharedUriDetail(context, uri)
                    ?: return@mapNotNull null
                return@mapNotNull when (singleUriData.fileType) {
                    IMAGE -> {
                        val newUri =
                            FileUtil.getSharedImageUri(context, uri) ?: return@mapNotNull null
                        singleUriData.toBuilder().uri(newUri).build()
                    }
                    GIF -> {
                        val newUri =
                            FileUtil.getSharedGifUri(context, uri) ?: return@mapNotNull null
                        singleUriData.toBuilder().uri(newUri).build()
                    }
                    VIDEO -> {
                        val newUri =
                            FileUtil.getSharedVideoUri(context, uri) ?: return@mapNotNull null
                        val thumbnailUri = FileUtil.getVideoThumbnailUri(context, uri)
                        singleUriData.toBuilder()
                            .uri(newUri)
                            .thumbnailUri(thumbnailUri)
                            .build()
                    }
                    PDF -> {
                        val newUri =
                            FileUtil.getSharedPdfUri(context, uri) ?: return@mapNotNull null
                        val thumbnailUri = MediaUtils.getDocumentPreview(context, uri)
                        singleUriData.toBuilder()
                            .uri(newUri)
                            .thumbnailUri(thumbnailUri)
                            .build()
                    }
                    AUDIO -> {
                        val newUri =
                            FileUtil.getSharedAudioUri(context, uri) ?: return@mapNotNull null
                        val thumbnailUri = FileUtil.getAudioThumbnail(context, uri)
                        singleUriData.toBuilder()
                            .uri(newUri)
                            .thumbnailUri(thumbnailUri)
                            .build()
                    }
                    else -> null
                }
            }
            _updatedUriDataList.postValue(dataList)
        }

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