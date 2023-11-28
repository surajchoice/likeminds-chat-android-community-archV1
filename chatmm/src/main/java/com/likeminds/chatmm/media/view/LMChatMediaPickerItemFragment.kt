package com.likeminds.chatmm.media.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailActionModeData
import com.likeminds.chatmm.databinding.FragmentMediaPickerItemBinding
import com.likeminds.chatmm.databinding.LmChatFragmentMediaPickerItemBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapter
import com.likeminds.chatmm.media.view.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.chatmm.utils.actionmode.ActionModeCallback
import com.likeminds.chatmm.utils.actionmode.ActionModeListener
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.model.ITEM_MEDIA_PICKER_HEADER
import com.likeminds.chatmm.utils.permissions.*


class LMChatMediaPickerItemFragment :
    BaseFragment<LmChatFragmentMediaPickerItemBinding, MediaViewModel>(),
    MediaPickerAdapterListener,
    ActionModeListener<ChatroomDetailActionModeData> {

    private var actionModeCallback: ActionModeCallback<ChatroomDetailActionModeData>? = null

    lateinit var mediaPickerAdapter: MediaPickerAdapter

    private lateinit var mediaPickerItemExtras: MediaPickerItemExtras
    private val selectedMedias by lazy { HashMap<String, MediaViewData>() }

    companion object {
        private const val BUNDLE_MEDIA_PICKER_ITEM = "bundle of media picker item"
        const val TAG = "MediaPickerItem"

        @JvmStatic
        fun getInstance(extras: MediaPickerItemExtras): LMChatMediaPickerItemFragment {
            val fragment = LMChatMediaPickerItemFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_PICKER_ITEM, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel>? {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): LmChatFragmentMediaPickerItemBinding {
        return LmChatFragmentMediaPickerItemBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaPickerItemExtras =
            MediaPickerItemFragmentArgs.fromBundle(requireArguments()).mediaPickerItemExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        if (mediaPickerItemExtras.allowMultipleSelect) {
            setupMenu()
        }
        initializeUI()
        initializeListeners()

        viewModel.fetchMediaInBucket(
            requireContext(),
            mediaPickerItemExtras.bucketId,
            mediaPickerItemExtras.mediaTypes as MutableList<String>
        ).observe(viewLifecycleOwner) {
            mediaPickerAdapter.replace(it)
        }
    }

    private fun initializeUI() {
        binding.toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.tvToolbarTitle.text = mediaPickerItemExtras.folderTitle

        mediaPickerAdapter = MediaPickerAdapter(this)
        binding.rvMediaSingle.apply {
            val mLayoutManager = GridLayoutManager(requireContext(), 3)
            mLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (mediaPickerAdapter.getItemViewType(position)) {
                        ITEM_MEDIA_PICKER_HEADER -> 3
                        else -> 1
                    }
                }
            }
            layoutManager = mLayoutManager
            adapter = mediaPickerAdapter
        }
    }

    private fun initializeListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    // sets up the menu item
    private fun setupMenu() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.media_picker_item_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.select_multiple -> {
                        startActionMode()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun startActionMode() {
        if (actionModeCallback == null) {
            actionModeCallback = ActionModeCallback()
        }
        if (actionModeCallback?.isActionModeEnabled() != true) {
            actionModeCallback?.startActionMode(
                this,
                requireActivity() as AppCompatActivity,
                R.menu.media_picker_actions_menu
            )
        }
        updateActionTitle()
    }

    private fun updateActionTitle() {
        if (selectedMedias.size > 0) {
            actionModeCallback?.updateTitle("${selectedMedias.size} selected")
        } else {
            actionModeCallback?.updateTitle("Tap photo to select")
        }
    }

    override fun onActionItemClick(item: MenuItem?) {
        when (item?.itemId) {
            R.id.menu_item_ok -> {
                sendSelectedMedia(selectedMedias.values.toMutableList())
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActionModeDestroyed() {
        selectedMedias.clear()
        mediaPickerAdapter.notifyDataSetChanged()
    }

    override fun onMediaItemClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        sendSelectedMedia(listOf(mediaViewData))
    }

    override fun onMediaItemLongClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        if (selectedMedias.containsKey(mediaViewData.uri.toString())) {
            selectedMedias.remove(mediaViewData.uri.toString())
        } else {
            selectedMedias[mediaViewData.uri.toString()] = mediaViewData
        }

        mediaPickerAdapter.notifyItemChanged(itemPosition)

        // Invalidate Action Menu Items
        if (selectedMedias.isNotEmpty()) {
            startActionMode()
        } else {
            actionModeCallback?.finishActionMode()
        }
    }

    override fun isMediaSelectionEnabled(): Boolean {
        return actionModeCallback?.isActionModeEnabled() == true
    }

    override fun isMediaSelected(key: String): Boolean {
        return selectedMedias.containsKey(key)
    }

    override fun isMultiSelectionAllowed(): Boolean {
        return mediaPickerItemExtras.allowMultipleSelect
    }

    fun onBackPressedFromFragment() {
        if (isMediaSelectionEnabled()) actionModeCallback?.finishActionMode()
        else findNavController().navigateUp()
    }

    private fun sendSelectedMedia(medias: List<MediaViewData>) {
        val extra = MediaPickerResult.Builder()
            .mediaPickerResultType(MEDIA_RESULT_PICKED)
            .mediaTypes(mediaPickerItemExtras.mediaTypes)
            .allowMultipleSelect(mediaPickerItemExtras.allowMultipleSelect)
            .medias(medias)
            .build()

        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(
                    LMChatMediaPickerActivity.ARG_MEDIA_PICKER_RESULT, extra
                )
            })
        }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }
}