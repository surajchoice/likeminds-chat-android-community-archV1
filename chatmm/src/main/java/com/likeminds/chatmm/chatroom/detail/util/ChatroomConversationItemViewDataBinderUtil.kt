package com.likeminds.chatmm.chatroom.detail.util

import com.likeminds.chatmm.R
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.databinding.ItemAudioBinding
import com.likeminds.chatmm.media.model.MEDIA_ACTION_NONE
import com.likeminds.chatmm.media.model.MEDIA_ACTION_PAUSE
import com.likeminds.chatmm.media.model.MEDIA_ACTION_PLAY
import com.likeminds.chatmm.utils.DateUtil
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show

object ChatroomConversationItemViewDataBinderUtil {

    fun initAudioItemView(binding: ItemAudioBinding, attachment: AttachmentViewData) {
        binding.apply {
            when (attachment.mediaState) {
                MEDIA_ACTION_NONE -> {
                    ivPlayPause.setImageResource(R.drawable.ic_play_grey)
                    ivPlayPause.isClickable = true
                    if (attachment.meta != null) {
                        tvAudioDuration.text =
                            if (!attachment.currentDuration.equals("00:00")) {
                                attachment.currentDuration
                            } else {
                                DateUtil.formatSeconds(
                                    attachment.meta.duration ?: 0
                                )
                            }
                    }
                    seekBar.isEnabled = true
                    ivAudioLogo.show()
                    progressBarBuffer.hide()
                    waveAnim.hide()
                }
                MEDIA_ACTION_PLAY -> {
                    ivPlayPause.setImageResource(R.drawable.ic_pause_grey)
                    ivPlayPause.isClickable = true
                    tvAudioDuration.text = attachment.currentDuration
                    ivAudioLogo.hide()
                    waveAnim.show()
                    progressBarBuffer.hide()
                    seekBar.apply {
                        isEnabled = true
                        isClickable = true
                    }
                    if (!waveAnim.isAnimating) {
                        waveAnim.playAnimation()
                    }
                }
                MEDIA_ACTION_PAUSE -> {
                    ivPlayPause.setImageResource(R.drawable.ic_play_grey)
                    ivPlayPause.isClickable = true
                    tvAudioDuration.text = attachment.currentDuration
                    ivAudioLogo.hide()
                    waveAnim.show()
                    progressBarBuffer.hide()
                    seekBar.apply {
                        isEnabled = true
                        isClickable = true
                    }
                    waveAnim.pauseAnimation()
                }
            }
        }
    }
}