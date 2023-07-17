package com.likeminds.chatmm.reactions.view

import android.content.Context
import android.util.DisplayMetrics
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import com.likeminds.chatmm.databinding.DialogReactionsBinding
import com.likeminds.chatmm.databinding.FragmentChatroomDetailBinding
import com.likeminds.chatmm.utils.ViewUtils

class ReactionPopup(
    private val chatroomDetailBinding: FragmentChatroomDetailBinding,
    private val activity: FragmentActivity
) : PopupWindow(activity), View.OnClickListener {

    private var conversationId: String? = null

    private var chatroomId: String? = null

    private lateinit var binding: DialogReactionsBinding
    private var reactionsClickListener: ReactionsClickListener? = null

    override fun setContentView(contentView: View?) {
        val layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding =
            DialogReactionsBinding.inflate(layoutInflater, null, false)

        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = ViewUtils.dpToPx(48 + 8)
        val margin = ViewUtils.dpToPx(25 * 2)

        this.width = width - margin
        this.height = height

        setBackgroundDrawable(null)
        isOutsideTouchable = true
        isFocusable = true
        elevation = 50.0f
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
        initClickListeners()

        super.setContentView(binding.root)
    }

    fun showPopup(conversationId: String, itemPosition: Int) {
        this.conversationId = conversationId
        this.chatroomId = null
        showPopupAtSpecificHeight(itemPosition)
    }

    /**
     * check position and then display popup accordingly
     */
    fun showChatroomReactionPopup(chatroomId: String, itemPosition: Int) {
        this.chatroomId = chatroomId
        this.conversationId = null
        showPopupAtSpecificHeight(itemPosition)
    }

    fun attachListener(parent: Fragment) {
        if (parent is ChatroomDetailFragment)
            reactionsClickListener = parent
    }

    private fun initClickListeners() {
        binding.apply {
            tvReactionRedHeart.setOnClickListener(this@ReactionPopup)
            tvReactionCryingLaugh.setOnClickListener(this@ReactionPopup)
            tvReactionWow.setOnClickListener(this@ReactionPopup)
            tvReactionCry.setOnClickListener(this@ReactionPopup)
            tvReactionAnger.setOnClickListener(this@ReactionPopup)
            tvReactionLike.setOnClickListener(this@ReactionPopup)
            ivMoreReactions.setOnClickListener(this@ReactionPopup)
        }
    }

    private fun reacted(reaction: TextView, conversationId: String?, chatroomId: String?) {
        if (conversationId != null) {
            reactionsClickListener?.reactionClicked(
                reaction.text.toString(),
                conversationId, true
            )
        }
        if (chatroomId != null) {
            reactionsClickListener?.reactionClicked(
                reaction.text.toString(),
                chatroomId, false
            )
        }
    }

    override fun onClick(v: View?) {
        binding.apply {
            when (v?.id) {
                tvReactionRedHeart.id -> {
                    reacted(tvReactionRedHeart, conversationId, chatroomId)
                }
                tvReactionCryingLaugh.id -> {
                    reacted(tvReactionCryingLaugh, conversationId, chatroomId)
                }
                tvReactionWow.id -> {
                    reacted(tvReactionWow, conversationId, chatroomId)
                }
                tvReactionCry.id -> {
                    reacted(tvReactionCry, conversationId, chatroomId)
                }
                tvReactionAnger.id -> {
                    reacted(tvReactionAnger, conversationId, chatroomId)
                }
                tvReactionLike.id -> {
                    reacted(tvReactionLike, conversationId, chatroomId)
                }
                ivMoreReactions.id -> {
                    if (chatroomId != null) {
                        reactionsClickListener?.moreReactionsClicked(
                            chatroomId, false
                        )
                    }
                    if (conversationId != null) {
                        reactionsClickListener?.moreReactionsClicked(
                            conversationId, true
                        )
                    }

                }
            }
        }
    }

    private fun showPopupAtSpecificHeight(itemPosition: Int) {
        val viewClicked =
            chatroomDetailBinding.rvChatroom.layoutManager?.findViewByPosition(
                itemPosition
            )
        val yCurrent = getViewStartingPoint(itemPosition)
        val yNext = getViewStartingPoint(itemPosition + 1)
        val fullHeight = chatroomDetailBinding.rvChatroom.height

        if (viewClicked != null) {
            if (viewClicked.height > fullHeight && yNext == 0) {
                this.showAtLocation(
                    chatroomDetailBinding.rvChatroom,
                    Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                    0,
                    fullHeight / 2
                )
            } else if (yCurrent < fullHeight / 2) {
                this.showAtLocation(
                    chatroomDetailBinding.rvChatroom,
                    Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                    0,
                    yCurrent + viewClicked.height
                )
            } else {
                this.showAtLocation(
                    chatroomDetailBinding.rvChatroom,
                    Gravity.CENTER_HORIZONTAL or Gravity.TOP,
                    0,
                    yCurrent - height
                )
            }
        } else {
            return
        }
    }

    fun getViewStartingPoint(itemPosition: Int): Int {
        val viewClicked =
            chatroomDetailBinding.rvChatroom.layoutManager?.findViewByPosition(
                itemPosition
            )
        val originalPos = IntArray(2)
        viewClicked?.getLocationInWindow(originalPos)
        return originalPos[1]
    }
}

interface ReactionsClickListener {
    fun reactionClicked(unicode: String, conversationId: String, isConversation: Boolean)
    fun moreReactionsClicked(conversationId: String?, isConversation: Boolean)
}