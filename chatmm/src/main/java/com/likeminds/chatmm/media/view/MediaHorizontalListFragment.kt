package com.likeminds.chatmm.media.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.FragmentMediaHorizontalListBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.util.MediaViewUtils
import com.likeminds.chatmm.media.view.adapter.ImageSwipeAdapterListener
import com.likeminds.chatmm.media.view.adapter.MediaSwipeAdapter
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.model.ITEM_IMAGE_SWIPE
import com.likeminds.chatmm.utils.model.ITEM_VIDEO_SWIPE
import javax.inject.Inject

class MediaHorizontalListFragment :
    BaseFragment<FragmentMediaHorizontalListBinding, MediaViewModel>(), ImageSwipeAdapterListener {

    private lateinit var mediaExtras: MediaExtras

    private lateinit var mediaSwipeAdapter: MediaSwipeAdapter
    private var isHeaderShowing = true

    private var overflowMenu: PopupMenu? = null

    private var downloadableContentTypes: List<String>? = null

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    companion object {
        private const val SCREEN_RECORD = "screen_record"
        const val TAG = "MediaHorizontalListFragment"
        private const val BUNDLE_MEDIA_HORIZONTAL = "bundle of media horizontal"

        @JvmStatic
        fun getInstance(extras: MediaExtras): MediaHorizontalListFragment {
            val fragment = MediaHorizontalListFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_HORIZONTAL, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentMediaHorizontalListBinding {
        return FragmentMediaHorizontalListBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaExtras =
            MediaHorizontalListFragmentArgs.fromBundle(requireArguments()).mediaExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        initViewPager()
        observeCommunity()
        getCommunity()
        binding.btnBack.setOnClickListener {
            activity?.finish()
        }
        binding.overflowMenu.setOnClickListener {
            showOverflowMenu(it)
        }
    }


    private fun getCommunity() {
//        mediaExtras.communityId?.let { viewModel.getCommunity(it) }
    }

    private fun observeCommunity() {
        // todo:
//        viewModel.communityLiveData.observe(viewLifecycleOwner) { communityViewData ->
//            downloadableContentTypes = communityViewData.downloadableContentType()
//            if (!mediaExtras.medias.isNullOrEmpty()) {
//                handleOverflowMenuIcon(
//                    downloadableContentTypes,
//                    mediaExtras.medias!![binding.viewPager.currentItem].viewType
//                )
//            }
//        }
    }

    private fun initViewPager() {
        binding.viewPager.isSaveEnabled = false
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        mediaSwipeAdapter = MediaSwipeAdapter(this)
        binding.viewPager.adapter = mediaSwipeAdapter

        //To disable over scroll animation
        binding.viewPager.getChildAt(0)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        mediaSwipeAdapter.replace(mediaExtras.medias?.sortedBy { it.index })

        if (!mediaExtras.medias.isNullOrEmpty()) {
            if (mediaExtras.medias!!.size > 1) {
                binding.dot.visibility = View.VISIBLE
                binding.tvSubTitle2.visibility = View.VISIBLE
            }
            val pos = mediaExtras.position ?: 0
            if (pos >= 0 && pos < mediaExtras.medias!!.size) {
                updateHeader(mediaExtras.medias!![pos], pos)
                binding.viewPager.currentItem = pos
            }
        }
    }

    private fun handleOverflowMenuIcon(
        downloadableContentTypes: List<String>?,
        viewType: Int,
    ) {
        if ((downloadableContentTypes?.contains(IMAGE) == true && viewType == ITEM_IMAGE_SWIPE) ||
            (downloadableContentTypes?.contains(VIDEO) == true && viewType == ITEM_VIDEO_SWIPE)
        ) {
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
        val position = binding.viewPager.currentItem
        val media = mediaSwipeAdapter.items()[position] as? MediaSwipeViewData ?: return
        // todo:
        val notificationIcon = R.drawable.ic_notification
        MediaViewUtils.saveToGallery(
            viewLifecycleOwner,
            requireActivity(),
            media.uri,
            notificationIcon
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateHeader(mediaSwipeViewData: MediaSwipeViewData?, position: Int) {
        binding.tvTitle.text = mediaSwipeViewData?.title
        val size = mediaExtras.medias?.size ?: 0
        if (size > 1) {
            binding.tvSubTitle.text = "${position + 1} of $size ${getMediaText()}"
            binding.tvSubTitle2.text = getSubTitle(mediaSwipeViewData)
        } else {
            binding.tvSubTitle.text = getSubTitle(mediaSwipeViewData)
        }
    }

    private fun getSubTitle(mediaSwipeViewData: MediaSwipeViewData?): String? {
        var subTitle = mediaSwipeViewData?.subTitle

        // Remove comma in start if added
        if (subTitle?.startsWith(",") == true && subTitle.length > 1) {
            subTitle = subTitle.substring(1, subTitle.length).trim()
        }
        return subTitle
    }

    private fun getMediaText(): String {
        val imagesCount = mediaExtras.medias?.filter {
            it.viewType == ITEM_IMAGE_SWIPE
        }?.size ?: 0
        val videosCount = mediaExtras.medias?.filter {
            it.viewType == ITEM_VIDEO_SWIPE
        }?.size ?: 0
        return if (imagesCount > 0 && videosCount > 0) {
            "multimedia"
        } else if (imagesCount > 0) {
            "photos"
        } else if (videosCount > 0) {
            "videos"
        } else {
            "media"
        }
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            updateHeader(mediaExtras.medias!![position], position)
            handleOverflowMenuIcon(
                downloadableContentTypes,
                mediaExtras.medias!![position].viewType
            )
        }
    }

    override fun doCleanup() {
        super.doCleanup()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    override fun onImageClicked() {
        if (isHeaderShowing) {
            binding.headerView.animate()
                .translationY(-binding.headerView.height.toFloat())
        } else {
            binding.headerView.animate().translationY(0f)
        }
        isHeaderShowing = !isHeaderShowing
    }

    override fun onImageViewed() {
        super.onImageViewed()
        viewModel.sendImageViewedEvent(
            mediaExtras.chatroomId,
            mediaExtras.communityId.toString(),
            mediaExtras.conversationId
        )
    }

    override fun onVideoClicked(mediaSwipeViewData: MediaSwipeViewData) {
        val extra = MediaExtras.Builder()
            .communityId(mediaExtras.communityId)
            .mediaScreenType(MEDIA_VIDEO_PLAY_SCREEN)
            .chatroomId(mediaExtras.chatroomId)
            .conversationId(mediaExtras.conversationId)
            .isSecretChatroom(mediaExtras.isSecretChatroom)
            .medias(
                listOf(
                    MediaSwipeViewData.Builder()
                        .dynamicViewType(ITEM_VIDEO_SWIPE)
                        .uri(mediaSwipeViewData.uri)
                        .type("video")
                        .thumbnail(mediaSwipeViewData.thumbnail)
                        .title(mediaSwipeViewData.title)
                        .subTitle(mediaSwipeViewData.subTitle)
                        .build()
                )
            )
            .build()
        MediaActivity.startActivity(
            requireContext(),
            extra
        )
    }
}