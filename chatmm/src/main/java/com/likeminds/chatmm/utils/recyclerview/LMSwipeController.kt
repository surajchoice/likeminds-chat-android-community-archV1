package com.likeminds.chatmm.utils.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.R
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.ItemConversationAudioBinding
import com.likeminds.chatmm.databinding.ItemConversationVoiceNoteBinding
import com.likeminds.chatmm.utils.AndroidUtils
import com.likeminds.chatmm.utils.customview.BaseRecyclerAdapter
import com.likeminds.chatmm.utils.customview.DataBoundViewHolder
import com.likeminds.chatmm.utils.model.*
import kotlin.math.abs
import kotlin.math.min

class LMSwipeController(
    private val context: Context,
    private val swipeControllerActions: SwipeControllerActions
) : ItemTouchHelper.Callback() {

    private val supportedViewTypes = listOf(
        ITEM_CONVERSATION,
        ITEM_CONVERSATION_LINK,
        ITEM_CONVERSATION_MULTIPLE_MEDIA,
        ITEM_CONVERSATION_SINGLE_IMAGE,
        ITEM_CONVERSATION_SINGLE_GIF,
        ITEM_CONVERSATION_SINGLE_PDF,
        ITEM_CONVERSATION_SINGLE_VIDEO,
        ITEM_CONVERSATION_POLL,
        ITEM_CONVERSATION_MULTIPLE_DOCUMENT,
        ITEM_CONVERSATION_AUDIO,
        ITEM_CONVERSATION_VOICE_NOTE,
        ITEM_CHAT_ROOM,
        ITEM_CHAT_ROOM_POLL,
        ITEM_CHAT_ROOM_ANNOUNCEMENT
    )

    private lateinit var imageDrawable: Drawable
    private lateinit var shareRound: Drawable

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private lateinit var mView: View
    private var dX = 0f

    private var replyButtonProgress: Float = 0.toFloat()
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false

    private var enableSwipe = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        mView = viewHolder.itemView
        imageDrawable = ContextCompat.getDrawable(context, R.drawable.ic_reply)!!
        shareRound = ContextCompat.getDrawable(context, R.drawable.ic_reply_background)!!

        val adapter = (viewHolder.bindingAdapter as BaseRecyclerAdapter<*>)
        val viewType = adapter.getItemViewType(viewHolder.bindingAdapterPosition)

        // Check for delete conversation. Disable swipe if message is deleted
        if (viewHolder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
            val item = adapter.items()[viewHolder.bindingAdapterPosition]
            var canSwipe = true
            if (item is ConversationViewData) {
                canSwipe = !item.isDeleted()
            }

            if (viewType == ITEM_CONVERSATION_AUDIO) {
                val binder =
                    (viewHolder as? DataBoundViewHolder<*>)?.binding as? ItemConversationAudioBinding
                if (binder?.audioView?.isProgressFocussed() == true) {
                    return 0
                }
            }

            if (viewType == ITEM_CONVERSATION_VOICE_NOTE) {
                val binder =
                    (viewHolder as? DataBoundViewHolder<*>)?.binding as? ItemConversationVoiceNoteBinding

                if (binder?.isProgressBarFocused == true) {
                    return 0
                }
            }

            return if (supportedViewTypes.contains(viewType) && enableSwipe && canSwipe) {
                makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT)
            } else {
                0
            }
        } else {
            return 0
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }

        if (mView.translationX < convertToDp(130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            this.dX = dX
            startTracking = true
        }
        currentItemViewHolder = viewHolder
        drawReplyButton(c)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL
                    || event.action == MotionEvent.ACTION_UP
            if (swipeBack && abs(mView.translationX) >= convertToDp(100)) {
                swipeControllerActions.showReplyUI(viewHolder.bindingAdapterPosition)
            }
            return@setOnTouchListener false
        }
    }

    private fun drawReplyButton(canvas: Canvas) {
        if (currentItemViewHolder == null) {
            return
        }
        val translationX = mView.translationX
        val newTime = System.currentTimeMillis()
        val dt = min(17, newTime - lastReplyButtonAnimationTime)
        lastReplyButtonAnimationTime = newTime
        val showing = translationX >= convertToDp(30)
        if (showing) {
            if (replyButtonProgress < 1.0f) {
                replyButtonProgress += dt / 180.0f
                if (replyButtonProgress > 1.0f) {
                    replyButtonProgress = 1.0f
                } else {
                    mView.invalidate()
                }
            }
        } else if (translationX <= 0.0f) {
            replyButtonProgress = 0f
            startTracking = false
            isVibrate = false
        } else {
            if (replyButtonProgress > 0.0f) {
                replyButtonProgress -= dt / 180.0f
                if (replyButtonProgress < 0.1f) {
                    replyButtonProgress = 0f
                } else {
                    mView.invalidate()
                }
            }
        }
        val alpha: Int
        val scale: Float
        if (showing) {
            scale = if (replyButtonProgress <= 0.8f) {
                1.2f * (replyButtonProgress / 0.8f)
            } else {
                1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f)
            }
            alpha = min(255f, 255 * (replyButtonProgress / 0.8f)).toInt()
        } else {
            scale = replyButtonProgress
            alpha = min(255f, 255 * replyButtonProgress).toInt()
        }
        shareRound.alpha = alpha

        imageDrawable.alpha = alpha
        if (startTracking) {
            if (!isVibrate && mView.translationX >= convertToDp(100)) {
                mView.performHapticFeedback(
                    HapticFeedbackConstants.KEYBOARD_TAP,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
                isVibrate = true
            }
        }

        val x: Int = if (mView.translationX > convertToDp(130)) {
            convertToDp(130) / 2
        } else {
            (mView.translationX / 2).toInt()
        }

        val y = (mView.top + mView.measuredHeight / 2).toFloat()
        shareRound.colorFilter =
            PorterDuffColorFilter(Color.parseColor("#c4c4c4"), PorterDuff.Mode.MULTIPLY)

        shareRound.setBounds(
            (x - convertToDp(18) * scale).toInt(),
            (y - convertToDp(18) * scale).toInt(),
            (x + convertToDp(18) * scale).toInt(),
            (y + convertToDp(18) * scale).toInt()
        )
        shareRound.draw(canvas)
        imageDrawable.setBounds(
            (x - convertToDp(12) * scale).toInt(),
            (y - convertToDp(11) * scale).toInt(),
            (x + convertToDp(12) * scale).toInt(),
            (y + convertToDp(10) * scale).toInt()
        )
        imageDrawable.draw(canvas)
        shareRound.alpha = 255
        imageDrawable.alpha = 255
    }

    private fun convertToDp(pixel: Int): Int {
        return AndroidUtils.dp(pixel.toFloat(), context)
    }

    fun setSwipeEnabled(enabled: Boolean) {
        enableSwipe = enabled
    }
}
