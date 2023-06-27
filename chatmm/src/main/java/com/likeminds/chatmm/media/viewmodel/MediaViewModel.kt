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
import com.likeminds.chatmm.utils.ValueUtils.filterThenMap
import com.likeminds.chatmm.utils.coroutine.launchDefault
import com.likeminds.chatmm.utils.coroutine.launchIO
import com.likeminds.chatmm.utils.file.util.FileUtil
import com.likeminds.chatmm.utils.model.BaseViewType
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

    private val localFolders by lazy { MutableLiveData<List<MediaFolderViewData>>() }
    private val bucketMedias by lazy { MutableLiveData<List<BaseViewType>>() }

    private val _audioByteArray by lazy { MutableLiveData<ByteArray>() }
    val audioByteArray: LiveData<ByteArray> = _audioByteArray

    private val _updatedUriDataList by lazy { MutableLiveData<List<SingleUriData>>() }
    val updatedUriDataList: LiveData<List<SingleUriData>> = _updatedUriDataList

    private val _documentPreviewLiveData by lazy { MutableLiveData<List<SingleUriData>>() }
    val documentPreviewLiveData: LiveData<List<SingleUriData>> = _documentPreviewLiveData

    private val _giphyMedia by lazy { MutableLiveData<Pair<Boolean, Uri?>>() }
    val giphyMedia: LiveData<Pair<Boolean, Uri?>> = _giphyMedia

    private val audioMediaList by lazy { ArrayList<MediaViewData>() }
    private val localAudioFileLists by lazy { MutableLiveData<List<BaseViewType>>() }

    private val localDocumentFiles by lazy { MutableLiveData<List<BaseViewType>>() }
    private val documentMediaList by lazy { ArrayList<MediaViewData>() }

    private val getMediaBrowserViewData by lazy { MediaBrowserViewData.Builder().build() }

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

    fun fetchAllAudioFiles(context: Context): LiveData<List<BaseViewType>> {
        mediaRepository.getLocalAudioFiles(context) { medias ->
            audioMediaList.clear()
            audioMediaList.addAll(medias)

            postAudioListForView(audioMediaList)
        }
        return localAudioFileLists
    }

    fun filterAudioByKeyword(keyword: String) {
        val keywordList = keyword.split(" ")
        val updatedList = audioMediaList.filterThenMap({ media ->
            val matchedKeywords = keywordList.filter {
                media.mediaName?.contains(it, true) == true
            }
            Pair(matchedKeywords.isNotEmpty(), matchedKeywords)
        }, {
            it.first.toBuilder().filteredKeywords(it.second).build()
        })

        postAudioListForView(updatedList)
    }

    fun clearAudioFilter() {
        postAudioListForView(audioMediaList)
    }

    private fun postAudioListForView(audioMediaList: List<MediaViewData>) {
        val mediaList = ArrayList<BaseViewType>()
        mediaList.addAll(audioMediaList)
        localAudioFileLists.value = mediaList
    }

    fun fetchAllFolders(
        context: Context,
        mediaTypes: List<String>,
    ): LiveData<List<MediaFolderViewData>> {
        mediaRepository.getLocalFolders(context, mediaTypes, localFolders::postValue)
        return localFolders
    }

    fun fetchMediaInBucket(
        context: Context,
        bucketId: String,
        mediaTypes: MutableList<String>,
    ): LiveData<List<BaseViewType>> {
        mediaRepository.getMediaInBucket(context, bucketId, mediaTypes) { medias ->
            val mediaList = ArrayList<BaseViewType>()
            var headerName = ""
            medias.forEach { media ->
                if (media.dateTimeStampHeader != headerName) {
                    mediaList.add(getMediaHeader(media.dateTimeStampHeader))
                    headerName = media.dateTimeStampHeader
                }
                mediaList.add(media)
            }
            bucketMedias.postValue(mediaList)
        }
        return bucketMedias
    }

    fun fetchAllDocuments(context: Context): LiveData<List<BaseViewType>> {
        mediaRepository.getLocalDocumentFiles(context) { medias ->
            // Update documents list to be used for various purpose like sorting
            documentMediaList.clear()
            documentMediaList.addAll(medias)

            sortDocumentsByName()
        }
        return localDocumentFiles
    }

    fun sortDocumentsByName() {
        documentMediaList.sortBy { it.mediaName }
        postDocumentListForView(documentMediaList)
    }

    fun sortDocumentsByDate() {
        documentMediaList.sortByDescending { it.date }
        postDocumentListForView(documentMediaList)
    }

    fun filterDocumentsByKeyword(keyword: String) {
        val keywordList = keyword.split(" ")
        val updatedList = documentMediaList.filterThenMap({ media ->
            val matchedKeywords = keywordList.filter {
                media.mediaName?.contains(it) == true
            }
            Pair(matchedKeywords.isNotEmpty(), matchedKeywords)
        }, {
            it.first.toBuilder().filteredKeywords(it.second).build()
        })

        postDocumentListForView(updatedList)
    }

    fun clearDocumentFilter() {
        postDocumentListForView(documentMediaList)
    }

    private fun postDocumentListForView(updatedList: List<MediaViewData>) {
        val mediaList = ArrayList<BaseViewType>()
        mediaList.add(getMediaBrowserViewData)
        mediaList.addAll(updatedList)
        localDocumentFiles.postValue(mediaList)
    }

    private fun getMediaHeader(title: String): MediaHeaderViewData {
        return MediaHeaderViewData.Builder().title(title).build()
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