package com.likeminds.chatmm.utils.file.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import com.likeminds.chatmm.utils.file.util.Constants.SDCard.SDCARD_PATHS
import java.io.File

object SDCardUtils {

    private val envExternalStorage = System.getenv("EXTERNAL_STORAGE").orEmpty()
    private val envSecondaryStorage = System.getenv("SECONDARY_STORAGE").orEmpty()
    private val envEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET").orEmpty()

    @Suppress("DEPRECATION")
    private val emulatedStorage: String
        get() {
            var rawStorageId = ""
            val path = Environment.getExternalStorageDirectory().absolutePath
            val folders = path.split(File.separator)
            val lastSegment = folders.last()
            if (lastSegment.isNotBlank() && TextUtils.isDigitsOnly(lastSegment)) {
                rawStorageId = lastSegment
            }
            return if (rawStorageId.isBlank()) {
                envEmulatedStorageTarget
            } else {
                envEmulatedStorageTarget + File.separator + rawStorageId
            }
        }

    private val secondaryStorage: List<String>
        get() = if (envSecondaryStorage.isNotBlank()) {
            envSecondaryStorage.split(File.pathSeparator)
        } else {
            listOf()
        }

    private val availableSDCardsPaths: List<String>
        get() {
            val availableSDCardsPaths = mutableListOf<String>()
            SDCARD_PATHS.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    availableSDCardsPaths.add(path)
                }
            }
            return availableSDCardsPaths
        }

    fun getStorageDirectories(context: Context): Array<String> {
        val availableDirectories = HashSet<String>()
        if (envEmulatedStorageTarget.isNotBlank()) {
            availableDirectories.add(emulatedStorage)
        } else {
            availableDirectories.addAll(getExternalStorage(context))
        }
        availableDirectories.addAll(secondaryStorage)
        return availableDirectories.toTypedArray()
    }

    private fun getExternalStorage(context: Context): Set<String> {
        val availableDirectories = HashSet<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val files = getExternalFilesDirs(context)
            files?.forEach { file ->
                val applicationSpecificAbsolutePath = file.absolutePath
                var rootPath = applicationSpecificAbsolutePath
                    .substring(9, applicationSpecificAbsolutePath.indexOf("Android/data"))
                rootPath = rootPath.substring(rootPath.indexOf("/storage/") + 1)
                rootPath = rootPath.substring(0, rootPath.indexOf("/"))
                if (rootPath != "emulated") {
                    availableDirectories.add(rootPath)
                }
            }
        } else {
            if (envExternalStorage.isBlank()) {
                availableDirectories.addAll(availableSDCardsPaths)
            } else {
                availableDirectories.add(envExternalStorage)
            }
        }
        return availableDirectories
    }

    private fun getExternalFilesDirs(context: Context): Array<File>? {
        return context.getExternalFilesDirs(null)
    }
}