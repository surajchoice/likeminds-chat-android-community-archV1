package com.likeminds.chatmm.utils.file.util

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.likeminds.chatmm.utils.file.util.Constants.PathUri.COLUMN_DATA
import com.likeminds.chatmm.utils.file.util.Constants.PathUri.COLUMN_DISPLAY_NAME
import com.likeminds.chatmm.utils.file.util.Constants.PathUri.FOLDER_DOWNLOAD
import com.likeminds.chatmm.utils.file.util.Paths.isDownloadsDocument
import com.likeminds.chatmm.utils.file.util.Paths.isExternalStorageDocument
import com.likeminds.chatmm.utils.file.util.Paths.isFile
import com.likeminds.chatmm.utils.file.util.Paths.isGooglePhotosUri
import com.likeminds.chatmm.utils.file.util.Paths.isMediaDocument
import com.likeminds.chatmm.utils.file.util.Paths.isMediaStore
import com.likeminds.chatmm.utils.file.util.Paths.isRawDownloadsDocument
import java.io.File

object PathUtils {

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    internal fun getPath(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        //Document Provider
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    uri.isExternalStorageDocument -> externalStorageDocument(context, uri)
                    uri.isRawDownloadsDocument -> rawDownloadsDocument(contentResolver, uri)
                    uri.isDownloadsDocument -> downloadsDocument(contentResolver, uri)
                    uri.isMediaDocument -> mediaDocument(contentResolver, uri)
                    else -> {
                        return ""
                    }
                }
            }
            // MediaStore (and general)
            uri.isMediaStore -> {
                return if (uri.isGooglePhotosUri) {
                    googlePhotosUri(uri) ?: ""
                } else {
                    ""
                }
            }
            uri.isFile -> uri.path ?: ""
            else -> ""
        }
    }

    /**
     * Method for external document
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun externalStorageDocument(context: Context, uri: Uri): String {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        if ("primary".equals(type, ignoreCase = true)) {
            return if (split.size > 1) {
                "${Environment.getExternalStorageDirectory()}/${split[1]}"
            } else {
                "${Environment.getExternalStorageDirectory()}/"
            }
        } else {
            val path = "storage/${docId.replace(":", "/")}"
            if (File(path).exists()) {
                return "/$path"
            }
            val availableExternalStorage = SDCardUtils.getStorageDirectories(context)
            var root = ""
            availableExternalStorage.forEach { storage ->
                root = if (split[1].startsWith("/")) {
                    "$storage${split[1]}"
                } else {
                    "$storage/${split[1]}"
                }
            }
            return if (root.contains(type)) {
                path
            } else {
                if (root.startsWith("/storage/") || root.startsWith("storage/")) {
                    root
                } else if (root.startsWith("/")) {
                    "/storage$root"
                } else {
                    "/storage/$root"
                }
            }
        }
    }

    /**
     * Method for rawDownloadDocument
     *
     */
    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun rawDownloadsDocument(contentResolver: ContentResolver, uri: Uri): String {
        val fileName = ContentUriUtils.getPathFromColumn(contentResolver, uri, COLUMN_DISPLAY_NAME)
        val subFolderName = FileUtil.getSubFolders(uri.toString())
        return if (fileName.isNotBlank()) {
            "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        } else {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                id.toLong()
            )
            ContentUriUtils.getPathFromColumn(contentResolver, contentUri, COLUMN_DATA)
        }
    }

    /**
     * Method for downloadsDocument
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun downloadsDocument(contentResolver: ContentResolver, uri: Uri): String {
        val fileName = ContentUriUtils.getPathFromColumn(contentResolver, uri, COLUMN_DISPLAY_NAME)
        val subFolderName = FileUtil.getSubFolders(uri.toString())
        if (fileName.isNotBlank()) {
            return "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        }
        var id = DocumentsContract.getDocumentId(uri)
        if (id.startsWith("raw:")) {
            id = id.replaceFirst("raw:".toRegex(), "")
            val file = File(id)
            if (file.exists()) return id
        } else if (id.startsWith("raw%3A%2F")) {
            id = id.replaceFirst("raw%3A%2F".toRegex(), "")
            val file = File(id)
            if (file.exists()) return id
        }
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"),
            id.toLong()
        )
        return ContentUriUtils.getPathFromColumn(contentResolver, contentUri, COLUMN_DATA)
    }

    /**
     * Method for MediaDocument
     *
     */
    @SuppressLint("NewApi")
    private fun mediaDocument(contentResolver: ContentResolver, uri: Uri): String {
        val docId = DocumentsContract.getDocumentId(uri)
        val split: Array<String?> = docId.split(":").toTypedArray()
        val contentUri: Uri =
            when (split[0]) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                //Todo test
                else -> MediaStore.Files.getContentUri(docId)
            }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return ContentUriUtils.getPathFromColumn(
            contentResolver,
            contentUri,
            COLUMN_DATA,
            selection,
            selectionArgs
        )
    }

    /**
     * Method for googlePhotos
     *
     */
    private fun googlePhotosUri(uri: Uri): String? {
        // Return the remote address
        return uri.lastPathSegment
    }
}