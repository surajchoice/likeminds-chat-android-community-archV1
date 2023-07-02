package com.likeminds.chatmm.chatroom.detail.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.R
import com.likeminds.chatmm.utils.ViewUtils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlin.math.roundToInt

internal class YouTubeVideoPlayerPopup(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val activity: Activity,
) : PopupWindow(context), CustomPlayerUiController.DragListener {

    private lateinit var binding: DialogMiniYoutubePlayerBinding
    private var videoPlayerListener: VideoPlayerListener? = null
    var customPlayerUiController: CustomPlayerUiController? = null
    var isFullScreen = false
    var videoId = ""

    override fun setContentView(contentView: View?) {
        val layoutInflater =
            this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding =
            DialogMiniYoutubePlayerBinding.inflate(layoutInflater, null, false)

        val fullWidth: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
        val height = ViewUtils.dpToPx(208)
        val margin = ViewUtils.dpToPx(16)
        this.width = fullWidth - margin
        this.height = height

        setBackgroundDrawable(null)
        initClickListeners()
        super.setContentView(binding.root)
    }

    private fun initClickListeners() {
        binding.ivClosePlayer.setOnClickListener {
            videoPlayerListener?.crossClicked()
            binding.youtubePlayerView.release()
        }
        binding.ivEnterExitFullscreen.setOnClickListener {
            isFullScreen = !isFullScreen
            enterExitFullScreen()
        }
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    formatSeconds(progress, binding.tvVideoCurrentTime)
                    customPlayerUiController?.seekVideo(progress.toFloat())
                }
            }
        })
    }

    fun setPopupWindowConstraints(isFullScreen: Boolean) {
        val set = ConstraintSet()
        val clRoot = binding.layoutYoutube
        set.clone(clRoot)
        set.clear(binding.youtubePlayerView.id, ConstraintSet.TOP)
        if (isFullScreen) {
            set.connect(
                binding.youtubePlayerView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                binding.youtubePlayerView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        } else {
            set.connect(
                binding.youtubePlayerView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            set.connect(
                binding.youtubePlayerView.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        }
        set.applyTo(clRoot)
    }

    fun setPopupWindowUI() {
        binding.ivEnterExitFullscreen.performClick()
    }

    fun formatSeconds(timeInSeconds: Int, textView: TextView) {
        val formattedTime = ViewUtils.formatSeconds(timeInSeconds)
        textView.text = formattedTime
    }

    fun showPopup(lifecycle: Lifecycle, videoId: String) {
        this.showAtLocation(
            recyclerView,
            Gravity.CENTER_HORIZONTAL or Gravity.TOP,
            0, ViewUtils.dpToPx(100)
        )
        initPlayer(lifecycle, videoId)
        initDraggable()
    }

    /**
     * play is set to true for playing video, and false to pause the video.
     */
    fun playOrPauseVideo(play: Boolean) {
        customPlayerUiController?.playOrPauseVideo(play)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initDraggable() {
        binding.layoutYoutube.setOnTouchListener(object : View.OnTouchListener {
            var orgY = 0
            var offsetY = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        orgY = event.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val x = binding.root.x.roundToInt()
                        offsetY = event.rawY.toInt() - orgY
                        this@YouTubeVideoPlayerPopup.update(x, offsetY, -1, -1, true)
                    }
                }
                return true
            }

        })
    }

    private fun initPlayer(lifecycle: Lifecycle, videoId: String) {
        this.videoId = videoId
        lifecycle.addObserver(binding.youtubePlayerView)

        val customPlayerUi =
            binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_youtube_video_player_layout)
        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                binding.progressBar.visibility = View.GONE
                customPlayerUiController = CustomPlayerUiController(
                    context,
                    customPlayerUi,
                    youTubePlayer
                )
                customPlayerUiController?.addDragListener(this@YouTubeVideoPlayerPopup)
                youTubePlayer.addListener(customPlayerUiController!!)
                youTubePlayer.loadVideo(videoId, 0f)
            }

            @SuppressLint("SetTextI18n")
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                super.onVideoDuration(youTubePlayer, duration)
                formatSeconds(duration.toInt(), binding.tvVideoDuration)
                binding.seekBar.max = duration.roundToInt()
            }

            @SuppressLint("SetTextI18n")
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                formatSeconds(second.toInt(), binding.tvVideoCurrentTime)
                binding.seekBar.progress = second.roundToInt()
            }
        })
    }

    fun attachListener(parent: Fragment) {
        videoPlayerListener = parent as VideoPlayerListener
    }

    internal interface VideoPlayerListener {
        fun crossClicked()
        fun fullScreenClicked(isFullScreen: Boolean)
    }

    private fun enterExitFullScreen() {
        customPlayerUiController?.fullscreen = isFullScreen

        if (isFullScreen) {
            customPlayerUiController?.enterFullScreen()
            binding.ivEnterExitFullscreen.setImageResource(R.drawable.ic_full_screen_exit)
            showSeekBar()
            binding.cardLayout.radius = 0f
        } else {
            customPlayerUiController?.exitFullScreen()
            binding.ivEnterExitFullscreen.setImageResource(R.drawable.ic_switch_to_full_screen)
            hideSeekBar()
            binding.cardLayout.radius = 20f
        }
        setPopupWindowConstraints(isFullScreen)
        videoPlayerListener?.fullScreenClicked(isFullScreen)
    }

    private fun showSeekBar() {
        binding.seekBar.visibility = View.VISIBLE
        binding.tvVideoDuration.visibility = View.VISIBLE
        binding.tvVideoCurrentTime.visibility = View.VISIBLE
    }

    private fun hideSeekBar() {
        binding.apply {
            seekBar.visibility = View.GONE
            tvVideoDuration.visibility = View.GONE
            tvVideoCurrentTime.visibility = View.GONE
        }
    }

    override fun dragged(y: Int) {
        val x = binding.root.x.roundToInt()
        this@YouTubeVideoPlayerPopup.update(x, y, -1, -1, true)
    }
}
