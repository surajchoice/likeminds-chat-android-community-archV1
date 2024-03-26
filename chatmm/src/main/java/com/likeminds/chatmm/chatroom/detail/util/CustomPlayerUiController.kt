package com.likeminds.chatmm.chatroom.detail.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.likeminds.chatmm.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlin.math.roundToInt

class CustomPlayerUiController(
    private val context: Context,
    customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer,
) : AbstractYouTubePlayerListener() {

    private var dragListener: DragListener? = null

    // panel is used to intercept clicks on the WebView, I don't want the user to be able to click the WebView directly.
    private var panel: View? = null
    private val playerTracker: YouTubePlayerTracker = YouTubePlayerTracker()
    private var horizontalProgress: LinearProgressIndicator? = null
    private var playPauseButton: ImageView? = null
    var fullscreen = false

    init {
        youTubePlayer.addListener(playerTracker)
        initViews(customPlayerUi)
    }

    private fun initViews(playerUi: View) {
        panel = playerUi.findViewById(R.id.panel)
        horizontalProgress = playerUi.findViewById(R.id.horizontal_progress)

        playPauseButton = playerUi.findViewById(R.id.iv_play_pause)
        playPauseButton?.setOnClickListener { _ ->
            if (playerTracker.state == PlayerState.PLAYING) {
                pauseVideo()
            } else {
                playVideo()
            }
        }
        initDraggable()
    }

    private fun pauseVideo() {
        youTubePlayer.pause()
        playPauseButton?.setImageResource(R.drawable.lm_chat_ic_play)
    }

    private fun playVideo() {
        youTubePlayer.play()
        playPauseButton?.setImageResource(R.drawable.lm_chat_ic_pause)
    }

    /**
     * play is set to true for playing video, and false to pause the video.
     */
    fun playOrPauseVideo(play: Boolean) {
        if (play) {
            playVideo()
        } else {
            pauseVideo()
        }
    }

    fun seekVideo(time: Float) {
        youTubePlayer.seekTo(time)
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
        if (state == PlayerState.PLAYING || state == PlayerState.PAUSED ||
            state == PlayerState.VIDEO_CUED || state == PlayerState.BUFFERING
        ) panel?.setBackgroundColor(
            ContextCompat.getColor(context, R.color.lm_chat_transparent)
        )
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        if (!fullscreen) {
            horizontalProgress!!.progress = second.roundToInt()
        }
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        if (!fullscreen) {
            horizontalProgress?.max = duration.roundToInt()
        }
    }

    fun enterFullScreen() {
        fullscreen = true
        horizontalProgress?.visibility = View.GONE
    }

    fun exitFullScreen() {
        fullscreen = false
        horizontalProgress?.visibility = View.VISIBLE
    }

    fun addDragListener(parent: DragListener) {
        this.dragListener = parent
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initDraggable() {
        panel?.setOnTouchListener(object : View.OnTouchListener {
            var orgY = 0
            var offsetY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        orgY = event.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        offsetY = event.rawY.toInt() - orgY
                        dragListener?.dragged(offsetY)
                        return true
                    }
                }
                return false
            }

        })
    }

    interface DragListener {
        fun dragged(y: Int)
    }
}