package com.likeminds.chatmm.utils.chrometabs

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class CustomTabService : Service() {

    // implemented blank service just to bind the chromium service

    companion object {
        private val sBinder: Binder = Binder()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return sBinder
    }

}