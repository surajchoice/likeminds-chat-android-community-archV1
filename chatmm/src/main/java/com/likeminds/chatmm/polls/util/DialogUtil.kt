package com.likeminds.chatmm.polls.util

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtil {
    fun showProfileNotExist(context: Context, text: String) {
        AlertDialog.Builder(context)
            .setTitle("Profile does not exist")
            .setCancelable(true)
            .setMessage(text)
            .setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }
}