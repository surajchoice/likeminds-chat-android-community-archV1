package com.likeminds.chatmm.chatroom.detail.view

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class ChatroomDetailActivity : BaseAppCompatActivity() {
    companion object {
        const val CHATROOM_DETAIL_EXTRAS = "CHATROOM_DETAIL_EXTRAS"

        @JvmStatic
        fun start(
            context: Context,
            chatroomDetailExtras: ChatroomDetailExtras,
            clipData: ClipData? = null,
        ) {
            val intent = Intent(context, ChatroomDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CHATROOM_DETAIL_EXTRAS, chatroomDetailExtras)
            intent.putExtra("bundle", bundle)
            if (clipData != null) {
                intent.clipData = clipData
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, chatroomDetailExtras: ChatroomDetailExtras): Intent {
            val intent = Intent(context, ChatroomDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CHATROOM_DETAIL_EXTRAS, chatroomDetailExtras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }
}