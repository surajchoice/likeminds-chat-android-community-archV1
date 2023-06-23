package com.likeminds.chatmm.media.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.likeminds.chatmm.R
import com.likeminds.chatmm.media.util.LMMediaPlayer.Companion.handler
import com.likeminds.chatmm.media.util.LMMediaPlayer.Companion.runnable

class MediaAudioForegroundService : Service(), MediaPlayerListener {
    private var mediaPlayer: LMMediaPlayer? = null
    private var progress: Int = 0
    var isDataSourceSet = false

    override fun onBind(intent: Intent?): IBinder {
        return MediaBinder()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initMediaPlayer()
        registerPlayNewAudio()
        registerSeekbarDragged()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationBuilder = createNotificationBuilder()
        val uri = intent?.extras?.getParcelable<Uri>(AUDIO_SERVICE_URI_EXTRA)
        progress = intent?.extras?.getInt(AUDIO_SERVICE_PROGRESS_EXTRA) ?: 0
        isDataSourceSet = true
        mediaPlayer?.setMediaDataSource(uri, progress = progress * 1000L)
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        return START_STICKY
    }

    private fun registerPlayNewAudio() {
        val filter = IntentFilter(Broadcast_PLAY_NEW_AUDIO)
        registerReceiver(newAudioBroadcastReceiver, filter)
    }

    private fun registerSeekbarDragged() {
        val filter = IntentFilter(Broadcast_SEEKBAR_DRAGGED)
        registerReceiver(seekbarDraggedReceiver, filter)
    }

    override fun onAudioComplete() {
        super.onAudioComplete()
        isDataSourceSet = false
        stopMedia()
        val completeIntent = Intent(Broadcast_COMPLETE)
        completeIntent.putExtra(AUDIO_IS_COMPLETE_EXTRA, true)
        sendBroadcast(completeIntent)
        stopSelf()
        stopForeground(true)
        removeHandler()
    }

    private val newAudioBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            isDataSourceSet = true
            val data = intent?.extras?.getParcelable<Uri>(AUDIO_SERVICE_URI_EXTRA)
            progress = intent?.extras?.getInt(AUDIO_SERVICE_PROGRESS_EXTRA) ?: 0
            val notificationBuilder = createNotificationBuilder()
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
            mediaPlayer?.setMediaDataSource(data, progress = progress * 1000L)
        }
    }

    private val seekbarDraggedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val progress = intent?.extras?.getInt(PROGRESS_SEEKBAR_DRAGGED) ?: 0
            mediaPlayer?.seekTo(progress * 1000L)
        }

    }

    fun removeHandler() {
        handler?.removeCallbacks(runnable ?: Runnable { })
    }

    override fun onProgressChanged(currentDuration: String, playedPercentage: Int) {
        super.onProgressChanged(currentDuration, playedPercentage)
        val progressIntent = Intent(Broadcast_PROGRESS)
        progressIntent.putExtra(AUDIO_DURATION_STRING_EXTRA, currentDuration)
        progressIntent.putExtra(AUDIO_DURATION_INT_EXTRA, playedPercentage)
        sendBroadcast(progressIntent)
    }

    fun isPlaying() = mediaPlayer?.isPlaying()

    fun playAudio() {
        if (mediaPlayer?.isPlaying() == false) {
            mediaPlayer?.start()
        }
    }

    fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying() == true) {
            mediaPlayer?.stop()
        }
    }

    fun pauseAudio() {
        if (mediaPlayer?.isPlaying() == true) {
            mediaPlayer?.pause()
        }
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).apply {
            setContentTitle(getString(R.string.audio_is_played))
            setSmallIcon(R.drawable.ic_notification)
            setAutoCancel(false)
            setSilent(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            priority = NotificationCompat.PRIORITY_LOW
        }
    }

    private fun initMediaPlayer() {
        mediaPlayer = LMMediaPlayer(this.applicationContext, this)
        handler = Handler(Looper.getMainLooper())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(false)
                enableLights(false)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        unregisterReceiver(newAudioBroadcastReceiver)
        unregisterReceiver(seekbarDraggedReceiver)
        mediaPlayer = null
        removeHandler()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "notification_media_player"
        private const val CHANNEL_NAME = "Media Player Notifications"
        private const val CHANNEL_DESCRIPTION =
            "Receive notifications when you play any audio file in chatroom"
        private var NOTIFICATION_ID = System.currentTimeMillis().toInt()

        const val AUDIO_DURATION_STRING_EXTRA = "audio duration string"
        const val AUDIO_DURATION_INT_EXTRA = "audio duration int"
        const val AUDIO_IS_COMPLETE_EXTRA = "audio isComplete"
        const val AUDIO_IS_BUFFER_EXTRA = "audio isBuffer"
        const val AUDIO_SERVICE_URI_EXTRA = "extra uri of audio"
        const val AUDIO_SERVICE_PROGRESS_EXTRA = "extra progress of audio"

        const val Broadcast_PROGRESS = "com.collabmates.app.Progress"
        const val Broadcast_BUFFER = "com.collabmates.app.Buffer"
        const val Broadcast_COMPLETE = "com.collabmates.app.Complete"
        const val PROGRESS_SEEKBAR_DRAGGED = "progress of seekbar after drag"
        const val Broadcast_PLAY_NEW_AUDIO = "com.collabmates.app.PlayNewAudio"
        const val Broadcast_SEEKBAR_DRAGGED = "com.collabmates.app.SeekbarDragged"
    }

    inner class MediaBinder : Binder() {
        fun getService(): MediaAudioForegroundService {
            return this@MediaAudioForegroundService
        }
    }
}