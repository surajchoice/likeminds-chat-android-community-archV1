package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.collabmates.sdk.media.model.MediaType
import com.likeminds.chatmm.media.adapter.MediaPickerAdapter
import com.likeminds.chatmm.media.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.util.LMMediaPlayer
import com.likeminds.chatmm.media.util.LMMediaPlayer.Companion.handler
import com.likeminds.chatmm.media.util.LMMediaPlayer.Companion.isDataSourceSet
import com.likeminds.chatmm.media.util.LMMediaPlayer.Companion.runnable
import com.likeminds.chatmm.media.util.MediaPlayerListener
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.likemindschat.BrandingData
import com.likeminds.likemindschat.R
import com.likeminds.likemindschat.SDKApplication
import com.likeminds.likemindschat.base.BaseFragment
import com.likeminds.likemindschat.databinding.FragmentMediaPickerAudioBinding
import com.likeminds.likemindschat.search.util.CustomSearchBar
import com.likeminds.likemindschat.utils.ViewUtils


internal class MediaPickerAudioFragment :
    BaseFragment<FragmentMediaPickerAudioBinding, MediaViewModel>(),
    MediaPickerAdapterListener, MediaPlayerListener {
    private lateinit var mediaPickerAdapter: MediaPickerAdapter

    private var mediaPlayer: LMMediaPlayer? = null

    private val fragmentActivity by lazy {
        activity as AppCompatActivity?
    }

    companion object {
        const val TAG = "MediaPickerAudio"
        private const val BUNDLE_MEDIA_PICKER_AUDIO = "bundle of media picker audio"

        @JvmStatic
        fun getInstance(extras: MediaPickerExtras): MediaPickerAudioFragment {
            val fragment = MediaPickerAudioFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_PICKER_AUDIO, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentMediaPickerAudioBinding {
        return FragmentMediaPickerAudioBinding.inflate(layoutInflater)
    }

    private lateinit var mediaPickerExtras: MediaPickerExtras
    private val selectedMedias by lazy { HashMap<String, MediaViewData>() }
    private var localItemPosition: Int = 0

    override fun drawPrimaryColor(color: Int) {
        super.drawPrimaryColor(color)

        binding.toolbar.setBackgroundColor(Color.WHITE)
        binding.fabSend.backgroundTintList = ColorStateList.valueOf(color)

        binding.tvToolbarTitle.setTextColor(Color.BLACK)
        binding.tvToolbarSubtitle.setTextColor(Color.BLACK)
        binding.ivBack.imageTintList = ColorStateList.valueOf(Color.BLACK)

        binding.toolbar.navigationIcon?.setTint(Color.BLACK)
        binding.toolbar.overflowIcon?.setTint(Color.BLACK)
    }

    override fun drawAdvancedColor(headerColor: Int, buttonsIconsColor: Int, textLinksColor: Int) {
        super.drawAdvancedColor(headerColor, buttonsIconsColor, textLinksColor)
        binding.toolbar.setBackgroundColor(headerColor)
        binding.fabSend.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)

        binding.tvToolbarTitle.setTextColor(Color.WHITE)
        binding.tvToolbarSubtitle.setTextColor(Color.WHITE)
        binding.ivBack.imageTintList = ColorStateList.valueOf(Color.WHITE)

        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        binding.toolbar.overflowIcon?.setTint(Color.WHITE)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeMediaPlayer()
        initializeUI()
        initializeListeners()
        setHasOptionsMenu(true)
    }

    override fun onProgressChanged(
        playedPercentage: Int,
    ) {
        val item = mediaPickerAdapter.items()?.get(localItemPosition) as MediaViewData

        mediaPickerAdapter.update(
            localItemPosition,
            item.toBuilder()
                .audioProgress(playedPercentage)
                .build()
        )
    }

    override fun observeData() {
        super.observeData()
        viewModel.fetchAllAudioFiles(requireContext()).observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mediaPickerAdapter.replace(it)
            } else {
                ViewUtils.showShortToast(requireContext(), getString(R.string.no_audio_files))
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaPickerExtras =
            MediaPickerDocumentFragmentArgs.fromBundle(requireArguments()).mediaPickerExtras
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer?.isPlaying() == true) {
            mediaPlayer?.pause()
            val mediaPlayed = mediaPickerAdapter.items()?.get(localItemPosition) as MediaViewData
            handler?.removeCallbacks(runnable ?: Runnable { })
            updateItem(
                localItemPosition, mediaPlayed.toBuilder()
                    .audioProgress(
                        mediaPlayer?.playedPercentage()
                    )
                    .playOrPause(MEDIA_ACTION_PAUSE)
                    .build()
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.media_picker_audio_menu, menu)
        updateMenu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateMenu(menu: Menu) {
        val isBrandingBasic = BrandingData.isBrandingBasic

        val item = menu.findItem(R.id.menuItemSearch)
        item?.icon?.setTint(if (isBrandingBasic) Color.BLACK else Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemSearch -> {
                showSearchToolbar()
            }
            else -> return false
        }
        return true
    }

    override fun onMediaItemClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        if (isMultiSelectionAllowed()) {
            if (selectedMedias.containsKey(mediaViewData.uri().toString())) {
                selectedMedias.remove(mediaViewData.uri().toString())
            } else {
                selectedMedias[mediaViewData.uri().toString()] = mediaViewData
            }

            mediaPickerAdapter.notifyItemChanged(itemPosition)

            updateSelectedCount()
        } else {
            sendSelectedMedias(listOf(mediaViewData))
        }
    }

    override fun isMediaSelectionEnabled(): Boolean {
        return selectedMedias.isNotEmpty()
    }

    override fun isMediaSelected(key: String): Boolean {
        return selectedMedias.containsKey(key)
    }

    override fun isMultiSelectionAllowed(): Boolean {
        return mediaPickerExtras.allowMultipleSelect
    }

    override fun onAudioComplete() {
        super.onAudioComplete()
        handler?.removeCallbacks(runnable ?: Runnable { })
        val playedAudio = mediaPickerAdapter.items()?.get(localItemPosition) as MediaViewData
        updateItem(
            localItemPosition, playedAudio.toBuilder()
                .audioProgress(0)
                .playOrPause(MEDIA_ACTION_NONE)
                .build()
        )
    }

    override fun onAudioActionClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        if (localItemPosition != itemPosition) {
            val previousPlayed =
                mediaPickerAdapter.items()?.get(localItemPosition) as MediaViewData

            if (previousPlayed.playOrPause() == MEDIA_ACTION_PLAY) {
                handler?.removeCallbacks(runnable ?: Runnable { })
            }
            updateItem(
                localItemPosition, previousPlayed.toBuilder()
                    .audioProgress(0)
                    .playOrPause(MEDIA_ACTION_NONE)
                    .build()
            )
            isDataSourceSet = false
        }
        try {
            when (mediaViewData.playOrPause()) {
                MEDIA_ACTION_PLAY -> {
                    mediaPlayer?.pause()
                    updateItem(
                        itemPosition, mediaViewData.toBuilder()
                            .audioProgress(
                                mediaPlayer?.playedPercentage()
                            )
                            .playOrPause(MEDIA_ACTION_PAUSE)
                            .build()
                    )
                }
                MEDIA_ACTION_PAUSE -> {
                    mediaPlayer?.start()
                    mediaPlayer?.setAudioProgress()
                    updateItem(
                        itemPosition, mediaViewData.toBuilder()
                            .playOrPause(MEDIA_ACTION_PLAY)
                            .build()
                    )
                }
                MEDIA_ACTION_NONE -> {
                    localItemPosition = itemPosition
                    mediaPlayer?.setMediaDataSource(mediaViewData.uri())
                    updateItem(
                        itemPosition, mediaViewData.toBuilder()
                            .playOrPause(MEDIA_ACTION_PLAY)
                            .build()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.stackTrace.toString())
        }
    }


    private fun initializeMediaPlayer() {
        mediaPlayer = LMMediaPlayer(requireContext(), this)
    }

    override fun doCleanup() {
        binding.searchBar.dispose()
        mediaPlayer?.release()
        mediaPlayer = null
        super.doCleanup()
    }

    private fun updateItem(position: Int, mediaItem: MediaViewData) {
        mediaPickerAdapter.update(
            position,
            mediaItem
        )
    }

    private fun initializeUI() {
        binding.toolbar.title = ""
        binding.tvToolbarTitle.text = ""

        fragmentActivity?.setSupportActionBar(binding.toolbar)

        initializeTitle()

        mediaPickerAdapter = MediaPickerAdapter(this)
        binding.recyclerView.apply {
            adapter = mediaPickerAdapter
        }
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        handler = Handler(Looper.getMainLooper())
        updateSelectedCount()

        initializeSearchView()
    }

    private fun initializeTitle() {
        binding.tvToolbarTitle.text =
            if (MediaType.isAudio(mediaPickerExtras.mediaTypes)
                && mediaPickerExtras.senderName?.isNotEmpty() == true
            ) {
                String.format("Send to %s", mediaPickerExtras.senderName)
            } else {
                getString(R.string.music)
            }
    }

    private fun updateSelectedCount() {
        if (isMediaSelectionEnabled()) {
            binding.tvToolbarSubtitle.text =
                String.format("%s selected", selectedMedias.size)
        } else {
            binding.tvToolbarSubtitle.text = getString(R.string.tap_to_select)
        }
        binding.fabSend.isVisible = isMediaSelectionEnabled()
    }

    private fun initializeSearchView() {
        binding.searchBar.setSearchViewListener(
            object : CustomSearchBar.SearchViewListener {
                override fun onSearchViewClosed() {
                    binding.searchBar.visibility = View.GONE
                    viewModel.clearAudioFilter()
                }

                override fun crossClicked() {
                    viewModel.clearAudioFilter()
                }

                override fun keywordEntered(keyword: String) {
                    viewModel.filterAudioByKeyword(keyword)
                }

                override fun emptyKeywordEntered() {
                    viewModel.clearAudioFilter()
                }
            }
        )
        binding.searchBar.observeSearchView(false)
    }

    private fun initializeListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.fabSend.setOnClickListener {
            sendSelectedMedias(selectedMedias.values.toList())
        }
    }

    private fun showSearchToolbar() {
        binding.searchBar.visibility = View.VISIBLE
        binding.searchBar.post {
            binding.searchBar.openSearch()
        }
    }

    private fun sendSelectedMedias(medias: List<MediaViewData>) {
        val extras = MediaPickerResult.Builder()
            .mediaPickerResultType(MEDIA_RESULT_PICKED)
            .mediaTypes(mediaPickerExtras.mediaTypes)
            .allowMultipleSelect(mediaPickerExtras.allowMultipleSelect)
            .medias(medias)
            .build()
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(MediaPickerActivity.ARG_MEDIA_PICKER_RESULT, extras)
            })
        }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    private fun clearSelectedMedias() {
        selectedMedias.clear()
        mediaPickerAdapter.notifyDataSetChanged()
        updateSelectedCount()
    }

    fun onBackPress(): Boolean {
        when {
            binding.searchBar.isOpen -> binding.searchBar.closeSearch()
            isMediaSelectionEnabled() -> clearSelectedMedias()
            mediaPlayer?.isPlaying() == true -> {
                mediaPlayer?.stop()
                val previousPlayed =
                    mediaPickerAdapter.items()?.get(localItemPosition) as MediaViewData
                updateItem(
                    localItemPosition, previousPlayed.toBuilder()
                        .audioProgress(0)
                        .playOrPause(MEDIA_ACTION_NONE)
                        .build()
                )
            }
            else -> return true
        }
        return false
    }

}