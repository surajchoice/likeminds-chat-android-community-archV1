package com.likeminds.chatmm.search.view

import android.content.Context
import android.content.Intent
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class SearchActivity : BaseAppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SearchActivity::class.java)
            context.startActivity(intent)
        }

        fun getIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}