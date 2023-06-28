package com.likeminds.chatmm.utils.mediauploader.worker

import com.likeminds.chatmm.utils.mediauploader.model.AWSFileResponse

object UploadHelper {
    private val awsFileResponses: ArrayList<AWSFileResponse> = arrayListOf()

    fun addAWSFileResponse(awsFileResponse: AWSFileResponse) {
        awsFileResponses.add(awsFileResponse)
    }

    fun removeAWSFileResponse(awsFileResponse: AWSFileResponse) {
        awsFileResponses.remove(awsFileResponse)
    }

    fun getAWSFileResponse(awsFolderPath: String?): AWSFileResponse? {
        if (awsFolderPath == null)
            return null
        for (awsFileResponse in awsFileResponses) {
            if (awsFileResponse.awsFolderPath == awsFolderPath)
                return awsFileResponse
        }
        return null
    }

    fun getAWSFileResponses(uuid: String?): List<AWSFileResponse> {
        return awsFileResponses.filter { it.uuid == uuid }
    }
}