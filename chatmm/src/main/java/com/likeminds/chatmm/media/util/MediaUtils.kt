package com.likeminds.chatmm.media.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import com.likeminds.chatmm.R
import com.likeminds.chatmm.media.model.MediaViewData
import com.likeminds.chatmm.media.model.SingleUriData
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.file.util.FileUtil
import com.likeminds.chatmm.utils.file.util.FileUtil.isLargeFile
import com.likeminds.chatmm.utils.file.util.MemoryUnitFormat

object MediaUtils {

    private const val TAG = "MediaUtils"

    /**
     * Fetches the pdf preview using Android's core [PdfRenderer]
     */
    fun getDocumentPreview(context: Context, uri: Uri): Uri? {
        try {
            //creates a file descriptor
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return null

            //creates an object of renderer of pdf
            val renderer = PdfRenderer(pfd)

            if (renderer.pageCount >= 0) {
                //opens first page
                val page = renderer.openPage(0)

                //create bitmap for the preview
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.ARGB_8888
                )

                //Make background white, if pdf is transparent
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, 0f, 0f, null)

                //render first page into the bitmap
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                //close the page
                page.close()

                //close the renderer
                renderer.close()

                //convert the bitmap into uri and return
                return FileUtil.getUriFromBitmapWithRandomName(context, bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "getDocumentPreview", e)
        }
        return null
    }

    /**
     * Return the file size in string format e.g. 10 MB
     * */
    fun getFileSizeText(sizeBytes: Long): String {
        return MemoryUnitFormat.formatBytes(sizeBytes)
    }

    fun isVideoType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("video/")
    }

    fun isImageType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("image/")
    }

    fun isPdfType(contentType: String?): Boolean {
        return null != contentType && contentType == "application/pdf"
    }

    fun isAudioType(contentType: String?): Boolean {
        return null != contentType && contentType.startsWith("audio/")
    }

    fun formatSeconds(timeInSeconds: Int): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours in 1..9) formattedTime += "0"
        if (hours > 0)
            formattedTime += "$hours:"
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        if (formattedTime.startsWith("00")) {
            return formattedTime.substring(1)
        }
        return formattedTime
    }

    fun convertMediaViewDataToSingleUriData(
        context: Context,
        medias: List<MediaViewData>?
    ): ArrayList<SingleUriData> {
        var largeFileSelected = false
        val mediaUris = arrayListOf<SingleUriData>()

        if (!medias.isNullOrEmpty()) {
            medias.forEach { mediaViewData ->
                if (!mediaViewData.size.isLargeFile) {
                    mediaUris.add(
                        SingleUriData.Builder()
                            .mediaName(mediaViewData.mediaName)
                            .uri(mediaViewData.uri)
                            .fileType(mediaViewData.mediaType)
                            .size(mediaViewData.size)
                            .duration(mediaViewData.duration)
                            .mediaName(mediaViewData.mediaName)
                            .duration(mediaViewData.duration)
                            .pdfPageCount(mediaViewData.pdfPageCount)
                            .build()
                    )
                } else {
                    largeFileSelected = true
                }
            }
        }

        if (largeFileSelected) {
            ViewUtils.showShortToast(
                context, context.getString(R.string.large_file_select_error_message)
            )
        }
        return mediaUris
    }

    fun getExternalIntentPickerUris(data: Intent?): ArrayList<Uri> {
        val uris = arrayListOf<Uri>()
        if (data != null) {
            val mediaUriCount = data.clipData?.itemCount ?: 0
            if (mediaUriCount > 0) {
                for (i in 0 until mediaUriCount) {
                    val mediaUri = data.clipData?.getItemAt(i)?.uri
                    if (mediaUri != null) uris.add(mediaUri)
                }
            } else {
                val mediaUri = data.data
                if (mediaUri != null) uris.add(mediaUri)
            }
        }
        return uris
    }
}