package com.likeminds.chatmm.polls.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.likeminds.chatmm.databinding.ActivityPollResultsBinding
import com.likeminds.chatmm.polls.model.PollResultExtras
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity

internal class PollResultsActivity : BaseAppCompatActivity() {
    private lateinit var binding: ActivityPollResultsBinding

    companion object {
        const val ARG_POLL_RESULTS = "ARG_POLL_RESULTS"

        @JvmStatic
        fun start(context: Context, extra: PollResultExtras) {
            val intent = Intent(context, PollResultsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_POLL_RESULTS, extra)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        fun getIntent(context: Context, extra: PollResultExtras): Intent {
            val intent = Intent(context, PollResultsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_POLL_RESULTS, extra)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // todo:
//        SDKApplication.getInstance().chatroomDetailComponent()?.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityPollResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}