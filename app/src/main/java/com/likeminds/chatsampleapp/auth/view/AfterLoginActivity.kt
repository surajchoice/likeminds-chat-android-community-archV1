package com.likeminds.chatsampleapp.auth.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.chatmm.LikeMindsChatUI
import com.likeminds.chatsampleapp.ChatMMApplication
import com.likeminds.chatsampleapp.R
import com.likeminds.chatsampleapp.auth.model.LoginExtra
import com.likeminds.chatsampleapp.auth.util.AuthPreferences
import com.likeminds.chatsampleapp.databinding.ActivityAfterLoginBinding

class AfterLoginActivity : AppCompatActivity() {

    private var extra: LoginExtra? = null

    private val authPreferences: AuthPreferences by lazy {
        AuthPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAfterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extra = intent.getParcelableExtra(ChatMMApplication.EXTRA_LOGIN)
        if (extra == null) {
            finish()
        }

        initCommunityTab()
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.community_tab -> {
                    initCommunityTab()
                }
                R.id.user -> {
                    initUserFragment()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun initUserFragment() {
        val fragment = UserFragment.getInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment, "UserFragment")
            .commit()
    }

    private fun initCommunityTab() {
        LikeMindsChatUI.initiateHomeFeed(
            this,
            R.id.frameLayout,
            authPreferences.getApiKey(),
            authPreferences.getUserName(),
            authPreferences.getUserId(),
            false
        ) {
            val userId = it?.user?.userUniqueId ?: ""
            authPreferences.saveUserId(userId)
        }
    }
}