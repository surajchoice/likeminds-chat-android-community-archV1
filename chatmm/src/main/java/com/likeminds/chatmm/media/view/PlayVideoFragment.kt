package com.likeminds.chatmm.media.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.PopupMenu
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.FragmentPlayVideoBinding
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.chatmm.media.model.MediaSwipeViewData
import com.likeminds.chatmm.media.model.VIDEO
import com.likeminds.chatmm.media.util.MediaViewUtils
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.BaseFragment
import javax.inject.Inject

class PlayVideoFragment : BaseFragment<FragmentPlayVideoBinding, MediaViewModel>() {

    private lateinit var mediaExtras: MediaExtras

    private var videoPlayer: ExoPlayer? = null
    private var overflowMenu: PopupMenu? = null

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    companion object {
        private const val SCREEN_RECORD = "screen_record"

        const val TAG = "PlayVideoFragment"
        private const val BUNDLE_PLAY_FRAGMENT = "bundle of play fragment"

        @JvmStatic
        fun getInstance(extras: MediaExtras): PlayVideoFragment {
            val fragment = PlayVideoFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_PLAY_FRAGMENT, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentPlayVideoBinding {
        return FragmentPlayVideoBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaExtras = PlayVideoFragmentArgs.fromBundle(requireArguments()).mediaExtras
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        initPlayer()
        observeCommunity()
        getCommunity()
        binding.btnBack.setOnClickListener {
            activity?.finish()
        }
        binding.overflowMenu.setOnClickListener {
            showOverflowMenu(it)
        }
    }

    private fun initPlayer() {
        mediaExtras.medias?.firstOrNull()?.let { mediaSwipeViewData ->
            binding.textTitle.text = mediaSwipeViewData.title
            binding.textSubTitle.text = getSubTitle(mediaSwipeViewData)
            viewModel.sendVideoPlayedEvent(
                mediaExtras.chatroomId,
                mediaExtras.communityId.toString(),
                mediaExtras.conversationId,
                mediaSwipeViewData.type
            )
            mediaSwipeViewData.uri.let { videoUri ->
                videoPlayer = ExoPlayer.Builder(requireContext()).build()
                binding.playerView.player = videoPlayer
                buildMediaSource(videoUri).let { mediaSource ->
                    videoPlayer?.setMediaSource(mediaSource)
                    videoPlayer?.prepare()
                }
            }
        }
    }

    // todo:
    private fun getCommunity() {
//        mediaExtras.communityId?.let { viewModel.getCommunity(it) }
    }

    private fun observeCommunity() {
//        viewModel.communityLiveData.observe(viewLifecycleOwner) { communityViewData ->
//            handleOverflowMenuIcon(communityViewData.downloadableContentType())
//        }
    }

    private fun handleOverflowMenuIcon(
        downloadableContentTypes: MutableList<String>?,
    ) {
        if (downloadableContentTypes?.contains(VIDEO) == true) {
            binding.overflowMenu.visibility = View.VISIBLE
        } else {
            binding.overflowMenu.visibility = View.GONE
        }
        if (downloadableContentTypes?.contains(SCREEN_RECORD) == false)
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        else
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun showOverflowMenu(view: View) {
        if (overflowMenu == null) {
            overflowMenu = MediaViewUtils.getOverflowMenu(requireContext(), view) {
                when (it.itemId) {
                    R.id.menu_save -> {
                        saveToGallery()
                    }
                }
                return@getOverflowMenu true
            }
        }
        overflowMenu?.show()
    }

    private fun saveToGallery() {
        val media = mediaExtras.medias?.firstOrNull() ?: return
        // todo:
//        val notificationIcon = sdkPreferences.getNotificationIcon()
        val notificationIcon = R.drawable.ic_notification
        MediaViewUtils.saveToGallery(
            viewLifecycleOwner,
            requireActivity(),
            media.uri,
            notificationIcon
        )
    }

    private fun getSubTitle(mediaSwipeViewData: MediaSwipeViewData?): String? {
        var subTitle = mediaSwipeViewData?.subTitle

        // Remove comma in start if added
        if (subTitle?.startsWith(",") == true && subTitle.length > 1) {
            subTitle = subTitle.substring(1, subTitle.length).trim()
        }
        return subTitle
    }

    private fun buildMediaSource(videoUri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(requireContext())
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUri))
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        videoPlayer?.playWhenReady = false
        videoPlayer?.release()
    }

}