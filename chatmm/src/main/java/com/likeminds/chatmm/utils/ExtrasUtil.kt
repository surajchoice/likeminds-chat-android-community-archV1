package com.likeminds.chatmm.utils

import android.os.Build
import android.os.Bundle
import androidx.core.os.BundleCompat

object ExtrasUtil {

    //returns the extras passed with if-else check
    @Suppress("DEPRECATION")
    fun <T> getParcelable(argument: Bundle?, argumentName: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (argument != null) {
                BundleCompat.getParcelable(argument, argumentName, clazz)
            } else {
                null
            }
        } else {
            argument?.getParcelable(argumentName)
        }
    }
}