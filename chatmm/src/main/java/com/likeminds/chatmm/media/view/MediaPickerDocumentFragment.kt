package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.likeminds.chatmm.media.adapter.MediaPickerAdapter
import com.likeminds.chatmm.media.adapter.MediaPickerAdapterListener
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.likemindschat.BrandingData
import com.likeminds.likemindschat.R
import com.likeminds.likemindschat.SDKApplication
import com.likeminds.likemindschat.base.BaseFragment
import com.likeminds.likemindschat.databinding.FragmentMediaPickerDocumentBinding
import com.likeminds.likemindschat.search.util.CustomSearchBar

class MediaPickerDocumentFragment :
    BaseFragment<FragmentMediaPickerDocumentBinding, MediaViewModel>(),
    MediaPickerAdapterListener {

    private lateinit var mediaPickerAdapter: MediaPickerAdapter

    private val fragmentActivity by lazy { activity as AppCompatActivity? }

    private val selectedMedias by lazy { HashMap<String, MediaViewData>() }
    private lateinit var mediaPickerExtras: MediaPickerExtras

    private var currentSort = SORT_BY_NAME

    companion object {
        const val TAG = "MediaPickerDocument"
        private const val BUNDLE_MEDIA_PICKER_DOC = "bundle of media picker doc"

        @JvmStatic
        fun getInstance(extras: MediaPickerExtras): MediaPickerDocumentFragment {
            val fragment = MediaPickerDocumentFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_PICKER_DOC, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel>? {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentMediaPickerDocumentBinding {
        return FragmentMediaPickerDocumentBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun drawPrimaryColor(color: Int) {
        super.drawPrimaryColor(color)

        binding.toolbar.setBackgroundColor(Color.WHITE)
        binding.fabSend.backgroundTintList = ColorStateList.valueOf(color)

        binding.toolbar.setTitleTextColor(Color.BLACK)
        binding.toolbar.setSubtitleTextColor(Color.BLACK)
        binding.toolbar.navigationIcon?.setTint(Color.BLACK)
        binding.toolbar.overflowIcon?.setTint(Color.BLACK)
    }

    override fun drawAdvancedColor(headerColor: Int, buttonsIconsColor: Int, textLinksColor: Int) {
        super.drawAdvancedColor(headerColor, buttonsIconsColor, textLinksColor)
        binding.toolbar.setBackgroundColor(headerColor)
        binding.fabSend.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaPickerExtras =
            MediaPickerDocumentFragmentArgs.fromBundle(requireArguments()).mediaPickerExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        setHasOptionsMenu(true)
        initializeUI()
        initializeListeners()
        viewModel.fetchAllDocuments(requireContext()).observe(viewLifecycleOwner) {
            mediaPickerAdapter.replace(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.media_picker_document_menu, menu)
        updateMenu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun updateMenu(menu: Menu) {
        val isBrandingBasic = BrandingData.isBrandingBasic

        //update search icon
        val item = menu.findItem(R.id.menuItemSearch)
        item?.icon?.setTint(if (isBrandingBasic) Color.BLACK else Color.WHITE)

        //update sort icon
        val item2 = menu.findItem(R.id.menuItemSort)
        item2?.icon?.setTint(if (isBrandingBasic) Color.BLACK else Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemSearch -> {
                showSearchToolbar()
            }
            R.id.menuItemSort -> {
                val menuItemView = requireActivity().findViewById<View>(item.itemId)
                showSortingPopupMenu(menuItemView)
            }
            else -> return false
        }
        return true
    }

    override fun doCleanup() {
        binding.searchBar.dispose()
        super.doCleanup()
    }

    private fun initializeUI() {
        binding.toolbar.title = ""
        fragmentActivity?.setSupportActionBar(binding.toolbar)

        mediaPickerAdapter = MediaPickerAdapter(this)
        binding.recyclerView.apply {
            adapter = mediaPickerAdapter
        }

        updateSelectedCount()

        initializeSearchView()
    }

    private fun initializeListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.fabSend.setOnClickListener {
            sendSelectedMedias(selectedMedias.values.toList())
        }
    }

    private fun sendSelectedMedias(medias: List<MediaViewData>) {
        val extra = MediaPickerResult.Builder()
            .isResultOk(true)
            .mediaPickerResultType(MEDIA_RESULT_PICKED)
            .mediaTypes(mediaPickerExtras.mediaTypes)
            .allowMultipleSelect(mediaPickerExtras.allowMultipleSelect)
            .medias(medias)
            .build()

        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(
                    MediaPickerActivity.ARG_MEDIA_PICKER_RESULT, extra
                )
            })
        }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    private fun updateSelectedCount() {
        if (isMediaSelectionEnabled()) {
            binding.textViewSelectedCount.text =
                String.format("%s selected", selectedMedias.size)
        } else {
            binding.textViewSelectedCount.text = getString(R.string.tap_to_select)
        }
        binding.fabSend.isVisible = isMediaSelectionEnabled()
    }

    private fun clearSelectedMedias() {
        selectedMedias.clear()
        mediaPickerAdapter.notifyDataSetChanged()
        updateSelectedCount()
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

    override fun browseDocumentClicked() {
        val extra = MediaPickerResult.Builder()
            .isResultOk(true)
            .mediaPickerResultType(MEDIA_RESULT_BROWSE)
            .mediaTypes(mediaPickerExtras.mediaTypes)
            .allowMultipleSelect(mediaPickerExtras.allowMultipleSelect)
            .build()
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(
                    MediaPickerActivity.ARG_MEDIA_PICKER_RESULT, extra
                )
            })
        }
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    override fun isMultiSelectionAllowed(): Boolean {
        return mediaPickerExtras.allowMultipleSelect
    }

    private fun showSortingPopupMenu(view: View) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.document_sort_menu, popup.menu)
        when (currentSort) {
            SORT_BY_NAME ->
                popup.menu.findItem(R.id.menuItemSortName).isChecked = true
            SORT_BY_DATE ->
                popup.menu.findItem(R.id.menuItemSortDate).isChecked = true
        }
        popup.setOnMenuItemClickListener { item ->
            item.isChecked = true
            when (item.itemId) {
                R.id.menuItemSortName -> {
                    if (currentSort != SORT_BY_NAME) {
                        currentSort = SORT_BY_NAME
                        viewModel.sortDocumentsByName()
                    }
                }
                R.id.menuItemSortDate -> {
                    if (currentSort != SORT_BY_DATE) {
                        currentSort = SORT_BY_DATE
                        viewModel.sortDocumentsByDate()
                    }
                }
            }
            true
        }
        popup.show()
    }

    private fun initializeSearchView() {
        binding.searchBar.setSearchViewListener(
            object : CustomSearchBar.SearchViewListener {
                override fun onSearchViewClosed() {
                    binding.searchBar.visibility = View.GONE
                    viewModel.clearDocumentFilter()
                }

                override fun crossClicked() {
                    viewModel.clearDocumentFilter()
                }

                override fun keywordEntered(keyword: String) {
                    viewModel.filterDocumentsByKeyword(keyword)
                }

                override fun emptyKeywordEntered() {
                    viewModel.clearDocumentFilter()
                }
            }
        )
        binding.searchBar.observeSearchView(false)
    }

    private fun showSearchToolbar() {
        binding.searchBar.visibility = View.VISIBLE
        binding.searchBar.post {
            binding.searchBar.openSearch()
        }
    }

    fun onBackPressedFromFragment(): Boolean {
        when {
            binding.searchBar.isOpen -> binding.searchBar.closeSearch()
            isMediaSelectionEnabled() -> clearSelectedMedias()
            else -> return true
        }
        return false
    }
}