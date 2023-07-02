package com.likeminds.chatmm.utils.mediauploader.worker

import android.content.Context
import androidx.work.*
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.utils.mediauploader.model.AWSFileResponse
import com.likeminds.chatmm.utils.mediauploader.model.GenericFileRequest
import com.likeminds.chatmm.utils.mediauploader.model.WORKER_SUCCESS
import java.util.concurrent.TimeUnit
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class ConversationMediaUploadWorker(
    context: Context, workerParams: WorkerParameters
) : MediaUploadWorker(context, workerParams) {

    private val conversationId by lazy { getStringParam(ARG_CONVERSATION_ID) }
    private val totalMediaCount by lazy { getIntParam(ARG_TOTAL_MEDIA_COUNT) }

    private lateinit var conversation: ConversationViewData

    companion object {
        const val ARG_CONVERSATION_ID = "ARG_CONVERSATION_ID"
        const val ARG_TOTAL_MEDIA_COUNT = "ARG_TOTAL_MEDIA_COUNT"

        fun getInstance(conversationId: String, totalMediaCount: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ConversationMediaUploadWorker>()
                .setInputData(
                    workDataOf(
                        ARG_CONVERSATION_ID to conversationId,
                        ARG_TOTAL_MEDIA_COUNT to totalMediaCount
                    )
                )
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag(conversationId)
                .build()
        }
    }

    override fun checkArgs() {
        require(ARG_CONVERSATION_ID)
        require(ARG_TOTAL_MEDIA_COUNT)
    }

    override fun init() {
        conversation = chatRoomDb.getConversation(conversationId)!!
    }

    override fun uploadFiles(continuation: Continuation<Int>) {
        val attachmentsToUpload = conversation.attachmentsToUpload()
        val thumbnailsToUpload = conversation.thumbnailsToUpload()
        val totalFilesToUpload = attachmentsToUpload?.size ?: 0

        if (attachmentsToUpload.isNullOrEmpty() && thumbnailsToUpload.isNullOrEmpty()) {
            continuation.resume(WORKER_SUCCESS)
            return
        }

        uploadList = createAWSRequestList(thumbnailsToUpload, attachmentsToUpload)
        uploadList.forEach { request ->
            val resumeAWSFileResponse =
                UploadHelper.getAWSFileResponse(request.awsFolderPath)
            if (resumeAWSFileResponse != null) {
                resumeAWSUpload(resumeAWSFileResponse, totalFilesToUpload, continuation, request)
            } else {
                createAWSUpload(request, totalFilesToUpload, continuation)
            }
        }
    }

    private fun resumeAWSUpload(
        resumeAWSFileResponse: AWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>,
        request: GenericFileRequest
    ) {
        val resume = transferUtility.resume(resumeAWSFileResponse.transferObserver!!.id)
        if (resume == null) {
            createAWSUpload(request, totalFilesToUpload, continuation)
        } else {
            uploadAWSFile(resumeAWSFileResponse, totalFilesToUpload, continuation)
        }
    }

    private fun createAWSUpload(
        request: GenericFileRequest,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val awsFileResponse = fileReceiver.uploadFile(request, conversation.uploadWorkerUUID)
        if (awsFileResponse != null) {
            UploadHelper.addAWSFileResponse(awsFileResponse)
            uploadAWSFile(awsFileResponse, totalFilesToUpload, continuation)
        }
    }

    private fun uploadAWSFile(
        awsFileResponse: AWSFileResponse,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        val observer = awsFileResponse.transferObserver ?: return
        setProgress(observer.id, observer.bytesTransferred, observer.bytesTotal)
        observer.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                onStateChanged(awsFileResponse, state, totalFilesToUpload, continuation)
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                setProgress(id, bytesCurrent, bytesTotal)
            }

            override fun onError(id: Int, ex: Exception?) {
                ex?.printStackTrace()
                failedIndex.add(awsFileResponse.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }
        })
    }

    private fun onStateChanged(
        response: AWSFileResponse,
        state: TransferState?,
        totalFilesToUpload: Int,
        continuation: Continuation<Int>
    ) {
        if (isStopped) {
            return
        }
        when (state) {
            TransferState.COMPLETED -> {
                UploadHelper.removeAWSFileResponse(response)
                val downloadUri = response.downloadUrl
                if (response.isThumbnail == true || response.hasThumbnail == true) {
                    if (thumbnailMediaMap.containsKey(response.index)) {
                        val value = thumbnailMediaMap[response.index]!!
                        if (value.first == null) {
                            thumbnailMediaMap[response.index] = Pair(downloadUri, value.second)
                        } else if (value.second == null) {
                            thumbnailMediaMap[response.index] = Pair(value.first, downloadUri)
                        }
                        uploadUrl(
                            thumbnailMediaMap[response.index],
                            totalMediaCount,
                            response,
                            totalFilesToUpload,
                            conversation,
                            continuation
                        )
                        thumbnailMediaMap.remove(response.index)
                    } else {
                        if (response.isThumbnail == true) {
                            thumbnailMediaMap[response.index] = Pair(null, downloadUri)
                        } else {
                            thumbnailMediaMap[response.index] = Pair(downloadUri, null)
                        }
                    }
                } else {
                    uploadUrl(
                        Pair(downloadUri, null),
                        totalMediaCount,
                        response,
                        totalFilesToUpload,
                        conversation,
                        continuation
                    )
                }
            }
            TransferState.FAILED -> {
                failedIndex.add(response.index)
                checkWorkerComplete(totalFilesToUpload, continuation)
            }
            else -> {

            }
        }
    }
}