package com.likeminds.chatmm.chatroom.detail.view

import android.app.Activity
import android.content.*
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.chatmm.*
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.databinding.ActivityChatroomDetailBinding
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class ChatroomDetailActivity : BaseAppCompatActivity() {

    private lateinit var binding: ActivityChatroomDetailBinding

    private var chatroomDetailExtras: ChatroomDetailExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val CHATROOM_DETAIL_EXTRAS = "CHATROOM_DETAIL_EXTRAS"
        const val FRAGMENT_TAG_CHATROOM_DETAIL = "FRAGMENT_TAG_CHATROOM_DETAIL"

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

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().chatroomDetailComponent()?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatroomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            chatroomDetailExtras = bundle.getParcelable(CHATROOM_DETAIL_EXTRAS)
            val args = Bundle().apply {
                putParcelable(CHATROOM_DETAIL_EXTRAS, chatroomDetailExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_chatroom_detail, args)
        } else {
            redirectActivity(true)
        }
    }

    override fun onBackPressed() {
        val chatroomDetailFragment = getChatroomDetailFragment()

        when {
            ViewUtils.getFragmentVisible(chatroomDetailFragment) -> {
                when {
                    chatroomDetailFragment?.consumeTouch() == true -> {
                        return
                    }

                    chatroomDetailExtras?.isFromSearchChatroom == true -> {
                        sendSearchChatroomClosedEvent()
                        redirectActivity(false)
                    }

                    chatroomDetailExtras?.isFromSearchMessage == true -> {
                        sendSearchMessageClosedEvent()
                        redirectActivity(false)
                    }

                    else -> {
                        chatroomDetailFragment?.setChatroomDetailActivityResult()
                        redirectActivity(false)
                    }
                }
            }

            else -> {
                setResult(Activity.RESULT_OK)
                redirectActivity(false)
            }
        }
    }

    private fun getChatroomDetailFragment(): ChatroomDetailFragment? {
        val chatroomDetailFragment = supportFragmentManager.findFragmentByTag(
            FRAGMENT_TAG_CHATROOM_DETAIL
        )
        return if (chatroomDetailFragment != null && chatroomDetailFragment is ChatroomDetailFragment) {
            chatroomDetailFragment
        } else {
            null
        }
    }

    private fun redirectActivity(isError: Boolean) {
        if (isError) {
            ViewUtils.showShortToast(
                this,
                getString(R.string.the_chatroom_link_is_either_tampered_or_invalid)
            )
        }
        supportFragmentManager.popBackStack()
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
    }

    // triggers an event when chatroom search is closed
    private fun sendSearchChatroomClosedEvent() {
        LMAnalytics.track(
            LMAnalytics.Events.CHATROOM_SEARCH_CLOSED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroomDetailExtras?.chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to chatroomDetailExtras?.communityId
            )
        )
    }

    // triggers an event when message search is closed
    private fun sendSearchMessageClosedEvent() {
        LMAnalytics.track(
            LMAnalytics.Events.MESSAGE_SEARCH_CLOSED,
            mapOf(
                LMAnalytics.Keys.CHATROOM_ID to chatroomDetailExtras?.chatroomId,
                LMAnalytics.Keys.COMMUNITY_ID to chatroomDetailExtras?.communityId
            )
        )
    }
}