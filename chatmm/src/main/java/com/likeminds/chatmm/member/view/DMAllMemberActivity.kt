package com.likeminds.chatmm.member.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity
import com.likeminds.chatmm.databinding.ActivityDmAllMembersBinding
import com.likeminds.chatmm.member.model.DMAllMemberExtras
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

class DMAllMemberActivity : BaseAppCompatActivity() {

    private lateinit var binding: ActivityDmAllMembersBinding

    private var chatroomDetailExtras: ChatroomDetailExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val DM_ALL_MEMBERS_EXTRAS = "DM_ALL_MEMBERS_EXTRAS"

        fun start(context: Context, extras: DMAllMemberExtras) {
            val intent = Intent(context, DMAllMemberActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(DM_ALL_MEMBERS_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        fun getIntent(context: Context, extras: DMAllMemberExtras): Intent {
            val intent = Intent(context, DMAllMemberActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(DM_ALL_MEMBERS_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().memberComponent()?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDmAllMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            chatroomDetailExtras = bundle.getParcelable(DM_ALL_MEMBERS_EXTRAS)
            val args = Bundle().apply {
                putParcelable(DM_ALL_MEMBERS_EXTRAS, chatroomDetailExtras)
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

    private fun redirectActivity(isError: Boolean) {
        if (isError) {
            ViewUtils.showShortToast(
                this,
                getString(R.string.something_went_wrong)
            )
        }
        supportFragmentManager.popBackStack()
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
    }
}