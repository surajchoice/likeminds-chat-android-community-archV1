package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.collabmates.membertagging.MemberTaggingDecoder
import com.collabmates.membertagging.MemberTaggingView
import com.collabmates.membertagging.model.MemberTaggingExtras
import com.collabmates.membertagging.util.MemberTaggingViewListener
import com.collabmates.sdk.auth.LoginPreferences
import com.collabmates.sdk.sdk.SDKPreferences
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.util.MediaUtils
import com.likeminds.chatmm.media.view.MediaActivity.Companion.BUNDLE_MEDIA_EXTRAS
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.likemindschat.BrandingData
import com.likeminds.likemindschat.R
import com.likeminds.likemindschat.SDKApplication
import com.likeminds.likemindschat.base.BaseFragment
import com.likeminds.likemindschat.base.customview.edittext.LikeMindsEditTextListener
import com.likeminds.likemindschat.chatroom.create.adapter.ImageAdapter
import com.likeminds.likemindschat.chatroom.create.adapter.ImageAdapterListener
import com.likeminds.likemindschat.databinding.FragmentConversationDocumentSendBinding
import com.likeminds.likemindschat.utils.*
import com.likeminds.likemindschat.utils.membertagging.MemberTaggingUtil
import javax.inject.Inject

internal class ConversationDocumentSendFragment :
    BaseFragment<FragmentConversationDocumentSendBinding, MediaViewModel>(),
    ImageAdapterListener {

    private lateinit var mediaExtras: MediaExtras
    private lateinit var documentURIs: ArrayList<SingleUriData>

    private lateinit var imageAdapter: ImageAdapter

    @Inject
    lateinit var loginPreferences: LoginPreferences

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    private lateinit var memberTagging: MemberTaggingView

    private var selectedPosition = 0
    private var selectedUri: SingleUriData? = null

    companion object {
        private const val TAG = "ConversationDocumentSendFragment"

        private const val BUNDLE_CONVERSATION_DOCUMENT_SEND = "bundle of conversation document edit"

        @JvmStatic
        fun getInstance(extras: MediaExtras): ConversationDocumentSendFragment {
            val fragment = ConversationDocumentSendFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_CONVERSATION_DOCUMENT_SEND, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel>? {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentConversationDocumentSendBinding {
        return FragmentConversationDocumentSendBinding.inflate(layoutInflater)
    }

    override fun drawPrimaryColor(color: Int) {
        super.drawPrimaryColor(color)
        binding.buttonSend.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun drawAdvancedColor(headerColor: Int, buttonsIconsColor: Int, textLinksColor: Int) {
        super.drawAdvancedColor(headerColor, buttonsIconsColor, textLinksColor)
        binding.buttonSend.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaExtras = ConversationMediaEditFragmentArgs.fromBundle(requireArguments()).mediaExtras
        val mediaUris = mediaExtras.mediaUris
        documentURIs = mediaUris as ArrayList<SingleUriData>
    }

    override fun setUpViews() {
        super.setUpViews()
        if (mediaExtras.isExternallyShared) {
            ProgressHelper.showProgress(binding.progressBar, true)
            viewModel.fetchExternallySharedUriData(
                requireContext(),
                documentURIs.map { it.uri() })
        } else {
            initRVForMedias(false)
        }

        initRichEditorSupport()
        initMemberTagging()

        MemberTaggingDecoder.decode(
            binding.etConversation,
            mediaExtras.text,
            BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                binding.root.context,
                R.color.pure_blue
            )
        )

        binding.buttonBack.setOnClickListener {
            val intent = Intent()
            viewModel.sendThirdPartyAbandoned(
                "file",
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.chatroomId.toString()
            )
            activity?.setResult(Activity.RESULT_CANCELED, intent)
            activity?.finish()
        }

        binding.buttonAdd.setOnClickListener {
            val extras = MediaPickerExtras.Builder()
                .senderName(mediaExtras.chatroomName ?: "Chatroom")
                .mediaTypes(listOf(PDF))
                .build()

            val intent = MediaPickerActivity.getIntent(requireContext(), extras)
            pickerLauncher.launch(intent)
        }

        binding.buttonSend.setOnClickListener {
            if (sdkPreferences.getIsGuestUser()) {
                SDKApplication.getLikeMindsCallback()?.loginRequiredCallback()
                activity?.finish()
            } else {
                initSendClick()
            }
        }

        binding.buttonDelete.setOnClickListener {
            deleteCurrentMedia()
        }
    }

    override fun observeData() {
        super.observeData()
        viewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }

        viewModel.updatedUriDataList.observe(viewLifecycleOwner) { dataList ->
            ProgressHelper.hideProgress(binding.progressBar)
            documentURIs.clear()
            documentURIs.addAll(dataList)
            initRVForMedias(true)
        }

        viewModel.getDocumentPreview().observe(viewLifecycleOwner) { uris ->
            ProgressHelper.hideProgress(binding.progressBar)
            selectedUri = if (documentURIs.size == 1) {
                val uri = uris.firstOrNull()
                if (uri != null) {
                    documentURIs.clear()
                    documentURIs.addAll(uris)
                }
                documentURIs.first()
            } else {
                val adapterItems = imageAdapter.items().filterIsInstance<SmallMediaViewData>()
                    .map { smallViewData ->
                        val uri = uris.firstOrNull { uriData ->
                            smallViewData.singleUriData().uri() == uriData.uri()
                        } ?: smallViewData.singleUriData()
                        smallViewData.toBuilder().singleUriData(uri).build()
                    }
                imageAdapter.replace(adapterItems)
                documentURIs.clear()
                documentURIs.addAll(adapterItems.map { it.singleUriData() })
                documentURIs[selectedPosition]
            }
            initMedia(selectedUri)
        }

    }

    //result callback for new document pick from custom gallery
    private val pickerLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result?.resultCode == Activity.RESULT_OK && result.data != null) {
            val mediaPickerResult =
                result.data?.extras?.getParcelable<MediaPickerResult>(MediaPickerActivity.ARG_MEDIA_PICKER_RESULT)
                    ?: return@registerForActivityResult
            when (mediaPickerResult.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    val intent = AndroidUtils.getExternalDocumentPickerIntent(
                        allowMultipleSelect = mediaPickerResult.allowMultipleSelect
                    )
                    browsePickerLauncher.launch(intent)
                }
                MEDIA_RESULT_PICKED -> {
                    val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                        requireContext(), mediaPickerResult.medias
                    )
                    updateMediaUris(mediaUris)
                }
            }
        }
    }

    private fun initRichEditorSupport() {
        binding.etConversation.addListener(object : LikeMindsEditTextListener {
            override fun onMediaSelected(contentUri: Uri, mimeType: String) {
            }
        })
    }

    private fun initMedia(singleUriData: SingleUriData?) {
        if (singleUriData == null) {
            return
        }
        invalidateDeleteMediaIcon(documentURIs.size)
        if (singleUriData.fileType() == PDF) {
            val name = singleUriData.name
            val pageCount = singleUriData.pdfPageCount() ?: 0
            val size = singleUriData.size() ?: 0
            val thumbnail = singleUriData.thumbnailUri()

            binding.textViewDocumentName.text = name
            if (pageCount > 0) {
                binding.textViewDocumentPageCount.show()
                binding.viewDotPageCount.show()
                binding.textViewDocumentPageCount.text =
                    getString(R.string.placeholder_pages, pageCount)
            } else {
                binding.textViewDocumentPageCount.hide()
                binding.viewDotPageCount.hide()
            }
            if (size > 0) {
                binding.textViewDocumentSize.show()
                binding.viewDotSize.show()
                binding.textViewDocumentSize.text = MediaUtils.getFileSizeText(size)
            } else {
                binding.textViewDocumentSize.hide()
                binding.viewDotSize.hide()
            }
            configureMetaDataView(thumbnail != null)
            if (thumbnail != null) {
                binding.imageViewDocumentIcon.setImageURI(singleUriData.thumbnailUri())
            }
        }
    }

    /**
     * Based on the document thumbnail, reconfigure the UI
     */
    private fun configureMetaDataView(hasThumbnail: Boolean) {
        val set = ConstraintSet()
        set.clone(binding.constraintLayout)
        if (hasThumbnail) {
            set.connect(
                binding.imageViewDocumentIcon.id,
                ConstraintSet.BOTTOM,
                binding.textViewDocumentName.id,
                ConstraintSet.TOP
            )
            set.connect(
                binding.textViewDocumentName.id,
                ConstraintSet.BOTTOM,
                binding.textViewDocumentSize.id,
                ConstraintSet.TOP
            )
            set.clear(binding.textViewDocumentName.id, ConstraintSet.TOP)
            set.setMargin(
                binding.textViewDocumentName.id,
                ConstraintSet.BOTTOM,
                ViewUtils.dpToPx(6)
            )
            set.connect(
                binding.textViewDocumentSize.id,
                ConstraintSet.BOTTOM,
                binding.buttonSend.id,
                ConstraintSet.TOP
            )
            set.clear(binding.textViewDocumentSize.id, ConstraintSet.TOP)
            set.setMargin(
                binding.textViewDocumentSize.id,
                ConstraintSet.BOTTOM,
                ViewUtils.dpToPx(16)
            )
        } else {
            set.connect(
                binding.imageViewDocumentIcon.id,
                ConstraintSet.BOTTOM,
                binding.bottomView.id,
                ConstraintSet.TOP
            )
            set.connect(
                binding.textViewDocumentName.id,
                ConstraintSet.TOP,
                binding.imageViewDocumentIcon.id,
                ConstraintSet.BOTTOM,
                1
            )
            set.setMargin(binding.textViewDocumentName.id, ConstraintSet.TOP, ViewUtils.dpToPx(16))
            set.clear(binding.textViewDocumentName.id, ConstraintSet.BOTTOM)
            set.connect(
                binding.textViewDocumentSize.id,
                ConstraintSet.TOP,
                binding.textViewDocumentName.id,
                ConstraintSet.BOTTOM
            )
            set.setMargin(binding.textViewDocumentSize.id, ConstraintSet.TOP, ViewUtils.dpToPx(6))
            set.clear(binding.textViewDocumentSize.id, ConstraintSet.BOTTOM)
        }
        set.applyTo(binding.constraintLayout)
        val lp = binding.imageViewDocumentIcon.layoutParams as ConstraintLayout.LayoutParams
        if (hasThumbnail) {
            lp.width = ConstraintLayout.LayoutParams.MATCH_PARENT
            lp.height = 0
            lp.setMargins(0, ViewUtils.dpToPx(16), 0, ViewUtils.dpToPx(16))
        } else {
            lp.width = ViewUtils.dpToPx(60)
            lp.height = ViewUtils.dpToPx(70)
        }
        binding.imageViewDocumentIcon.layoutParams = lp
    }

    private fun initRVForMedias(hasThumbnails: Boolean) {
        if (documentURIs.size == 1) {
            binding.rvMedias.visibility = View.GONE
        } else {
            initMediaDisplayRecyclerView()
        }
        selectedUri = documentURIs.first()
        initMedia(selectedUri)
        if (!hasThumbnails) {
            ProgressHelper.showProgress(binding.progressBar)
            viewModel.fetchDocumentPreview(requireContext(), documentURIs)
        }
    }

    private fun initMediaDisplayRecyclerView() {
        binding.rvMedias.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvMedias.layoutManager = linearLayoutManager
        imageAdapter = ImageAdapter(this)
        binding.rvMedias.adapter = imageAdapter
        imageAdapter.replace(documentURIs.mapIndexed { index, singleUriData ->
            val isSelected = index == 0
            SmallMediaViewData.Builder()
                .viewType(ITEM_DOCUMENT_SMALL)
                .singleUriData(singleUriData).isSelected(isSelected)
                .build()
        })
    }

    private fun invalidateDeleteMediaIcon(mediaFilesCount: Int) {
        binding.buttonDelete.isVisible = mediaFilesCount > 1
    }

    private fun deleteCurrentMedia() {
        documentURIs.removeAt(selectedPosition)
        if (documentURIs.size == 0) {
            binding.buttonBack.performClick()
            return
        } else {
            if (selectedPosition == documentURIs.size) {
                selectedPosition -= 1
            }
            val updatedMedias = documentURIs.mapIndexed { index, singleMediaUri ->
                val isSelected = index == selectedPosition
                val smallMediaViewData = SmallMediaViewData.Builder()
                    .viewType(ITEM_DOCUMENT_SMALL)
                    .singleUriData(singleMediaUri)
                    .isSelected(isSelected).build()
                if (isSelected) {
                    selectedUri = smallMediaViewData.singleUriData()
                }
                smallMediaViewData
            }
            imageAdapter.replace(updatedMedias)
            binding.rvMedias.isVisible = updatedMedias.size > 1
            initMedia(selectedUri)
        }
    }

    override fun mediaSelected(position: Int, smallMediaViewData: SmallMediaViewData) {
        if (selectedPosition == position) return
        showUpdatedPositionData(position, smallMediaViewData)
    }

    private fun showUpdatedPositionData(position: Int, viewData: SmallMediaViewData) {
        selectedPosition = position
        selectedUri = viewData.singleUriData()
        initMedia(selectedUri)
        val items = imageAdapter.items()
            .filterIsInstance(SmallMediaViewData::class.java)
            .mapIndexed { index, item ->
                item.toBuilder().isSelected(position == index).build()
            }
        imageAdapter.replace(items)
    }

    private fun initSendClick() {
        val text = memberTagging.replaceSelectedMembers(
            binding.etConversation.editableText
        )
        val intent = Intent()
        intent.putExtra(
            BUNDLE_MEDIA_EXTRAS,
            mediaExtras.toBuilder().mediaUris(documentURIs).conversation(text).build()
        )
        if (mediaExtras.isExternallyShared) {
            viewModel.sendThirdPartySharingEvent(
                "file",
                mediaExtras.chatroomType,
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.searchKey,
                mediaExtras.chatroomId.toString()
            )
        }
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun initMemberTagging() {
        memberTagging = binding.memberTaggingView
        memberTagging.initialize(
            MemberTaggingExtras.Builder()
                .editText(binding.etConversation)
                .darkMode(true)
                .color(
                    BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                        binding.etConversation.context,
                        R.color.pure_blue
                    )
                )
                .build()
        )

        memberTagging.addListener(object : MemberTaggingViewListener {
            override fun callApi(page: Int, searchName: String) {
                super.callApi(page, searchName)
                viewModel.fetchMembersForTagging(
                    mediaExtras.chatroomId,
                    page,
                    searchName
                )
            }
        })
    }

    //result callback for new document pick
    private val browsePickerLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val uris = MediaUtils.getExternalIntentPickerUris(result.data)
                viewModel.fetchUriDetails(requireContext(), uris) {
                    val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                        requireContext(), it
                    )
                    updateMediaUris(mediaUris, saveInCache = true)
                }
            }
        }

    fun onBackPressedFromFragment() {
        if (mediaExtras.isExternallyShared) {
            viewModel.sendThirdPartyAbandoned(
                "file",
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.chatroomId.toString()
            )
            findNavController().navigateUp()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun updateMediaUris(
        mediaUris: ArrayList<SingleUriData>,
        saveInCache: Boolean = false,
    ) {
        if (mediaUris.isNotEmpty()) {
            if (documentURIs.size == 1) {
                initMediaDisplayRecyclerView()
            }
            val uris = if (saveInCache) {
                AndroidUtils.moveAttachmentToCache(
                    requireContext(),
                    *mediaUris.toTypedArray()
                )

            } else {
                mediaUris
            }
            documentURIs.addAll(uris)
            imageAdapter.replace(documentURIs.mapIndexed { index, singleMediaUri ->
                val isSelected = index == selectedPosition
                SmallMediaViewData.Builder()
                    .viewType(ITEM_DOCUMENT_SMALL)
                    .singleUriData(singleMediaUri)
                    .isSelected(isSelected)
                    .build()
            })
            invalidateDeleteMediaIcon(documentURIs.size)
            ProgressHelper.showProgress(binding.progressBar)
            viewModel.fetchDocumentPreview(requireContext(), uris)
        }
    }
}