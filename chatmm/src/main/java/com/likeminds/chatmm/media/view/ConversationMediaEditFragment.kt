package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.collabmates.fileutil.FileUtil
import com.collabmates.membertagging.MemberTaggingDecoder
import com.collabmates.membertagging.MemberTaggingView
import com.collabmates.membertagging.model.MemberTaggingExtras
import com.collabmates.membertagging.util.MemberTaggingViewListener
import com.collabmates.sdk.auth.LoginPreferences
import com.collabmates.sdk.sdk.SDKPreferences
import com.likeminds.chatmm.media.customviews.ColorSeekBar
import com.likeminds.chatmm.media.customviews.MediaEditMode
import com.likeminds.chatmm.media.customviews.MediaEditMode.*
import com.likeminds.chatmm.media.customviews.interfaces.CanvasListener
import com.likeminds.chatmm.media.customviews.interfaces.OnTrimVideoListener
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
import com.likeminds.likemindschat.databinding.FragmentConversationMediaEditBinding
import com.likeminds.likemindschat.utils.AndroidUtils
import com.likeminds.likemindschat.utils.ProgressHelper
import com.likeminds.likemindschat.utils.ViewUtils
import com.likeminds.likemindschat.utils.getMediaType
import com.likeminds.likemindschat.utils.membertagging.MemberTaggingUtil
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import javax.inject.Inject

internal class ConversationMediaEditFragment :
    BaseFragment<FragmentConversationMediaEditBinding, MediaViewModel>(),
    ImageAdapterListener,
    ColorSeekBar.OnColorChangeListener, CanvasListener {

    private lateinit var mediaExtras: MediaExtras

    private lateinit var imageAdapter: ImageAdapter

    @Inject
    lateinit var loginPreferences: LoginPreferences

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    private lateinit var memberTagging: MemberTaggingView

    private var selectedPosition = 0
    private var selectedUri: SingleUriData? = null

    private var editMode: MediaEditMode? = null
    private var currentTextMode = -1
    private var currentTextSize = 1

    private val textSizes by lazy { MediaUtils.getTextSizes() }
    private val textTypefaces by lazy { MediaUtils.getTextTypeFaces(requireContext()) }
    private val textIcons by lazy { MediaUtils.getTextIcons() }

    companion object {
        const val TAG = "ConversationMediaEdit"
        private const val BUNDLE_CONVERSATION_MEDIA_EDIT = "bundle of conversation media edit"

        @JvmStatic
        fun getInstance(extras: MediaExtras): ConversationMediaEditFragment {
            val fragment = ConversationMediaEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_CONVERSATION_MEDIA_EDIT, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentConversationMediaEditBinding {
        return FragmentConversationMediaEditBinding.inflate(layoutInflater)
    }

    override val keepBindingRetained: Boolean
        get() = true

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
        if (arguments == null) return
        mediaExtras = ConversationMediaEditFragmentArgs.fromBundle(requireArguments()).mediaExtras
    }

    override fun handleResultListener() {
        super.handleResultListener()
        setFragmentResultListener(ImageCropFragment.REQUEST_KEY) { _, bundle ->
            val singleUriData =
                bundle.getParcelable<SingleUriData>(ImageCropFragment.BUNDLE_ARG_URI)
            initMedia(singleUriData)
            replaceItem(selectedPosition, singleUriData)
            mediaSelected(
                selectedPosition,
                SmallMediaViewData.Builder().singleUriData(singleUriData).build()
            )
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        if (mediaExtras.isExternallyShared) {
            ProgressHelper.showProgress(binding.progressBar, true)
            viewModel.fetchExternallySharedUriData(
                requireContext(),
                mediaExtras.mediaUris!!.map { it.uri() }
            )
        } else {
            initRVForMedias()
        }
        initRichEditorSupport()
        initMemberTagging()

        binding.colorSeekBar.setOnColorChangeListener(this)
        binding.canvas.setListener(this)

        MemberTaggingDecoder.decode(
            binding.etConversation,
            mediaExtras.text,
            BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                binding.etConversation.context,
                R.color.pure_blue
            )
        )

        binding.buttonBack.setOnClickListener {
            val intent = Intent()
            viewModel.sendThirdPartyAbandoned(
                "image/video",
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.chatroomId.toString()
            )
            activity?.setResult(Activity.RESULT_CANCELED, intent)
            activity?.finish()
        }

        binding.buttonAdd.setOnClickListener {
            val extra = MediaPickerExtras.Builder()
                .senderName(mediaExtras.chatroomName ?: "Chatroom")
                .mediaTypes(listOf(IMAGE, VIDEO))
                .build()
            val intent = MediaPickerActivity.getIntent(
                requireContext(),
                extra
            )
            pickerLauncher.launch(intent)
        }

        binding.buttonSend.setOnClickListener {
            if (sdkPreferences.getIsGuestUser()) {
                SDKApplication.getLikeMindsCallback()?.loginRequiredCallback()
                activity?.finish()
            } else {
                if (editMode != null) {
                    saveBitmapAndReset(
                        true,
                        VideoTrimExtras.Builder().initiatedSendMessage(true).build()
                    )
                } else {
                    initSendClick()
                }
            }
        }

        binding.buttonCropRotate.setOnClickListener {
            if (selectedUri == null) return@setOnClickListener
            saveBitmapAndReset(false)
            findNavController().navigate(
                ConversationMediaEditFragmentDirections.actionConversationMediaEditFragmentToImageCropFragment(
                    selectedUri!!
                )
            )
        }

        binding.buttonUndo.setOnClickListener {
            binding.canvas.undo()
        }

        binding.buttonText.setOnClickListener {
            if (editMode == TEXT) {
                changeTextMode()
            } else {
                if (editMode == DRAW) {
                    saveBitmapAndResetAndShowCanvas(TEXT)
                } else {
                    showCanvas(TEXT)
                }
                currentTextMode = -1
                changeTextMode()
                binding.inputText.visibility = View.VISIBLE
                binding.buttonTextIncrease.visibility = View.VISIBLE
                binding.buttonTextDecrease.visibility = View.VISIBLE
                binding.inputText.setTextColor(binding.colorSeekBar.getColor())
                ViewUtils.showKeyboard(requireContext(), binding.inputText)
                showColorChooser()
                showBackgroundDrawable(binding.colorSeekBar.getColor())
            }
        }

        binding.buttonDraw.setOnClickListener {
            if (editMode == DRAW) {
                saveBitmapAndReset(false)
            } else {
                if (editMode == TEXT) {
                    addTextToCanvas()
                    saveBitmapAndResetAndShowCanvas(DRAW)
                } else {
                    showCanvas(DRAW)
                }
                showColorChooser()
                showBackgroundDrawable(binding.colorSeekBar.getColor())
            }
        }

        binding.buttonTextIncrease.setOnClickListener {
            if (currentTextSize < textSizes.size - 1) {
                currentTextSize++
                changeTextSize()
            }
        }

        binding.buttonTextDecrease.setOnClickListener {
            if (currentTextSize > 0) {
                currentTextSize--
                changeTextSize()
            }
        }

        KeyboardVisibilityEvent.setEventListener(
            requireActivity(), viewLifecycleOwner
        ) { isOpen ->
            if (!isOpen && binding.inputText.visibility == View.VISIBLE && editMode == TEXT) {
                addTextToCanvas()
            }
        }

        binding.buttonDelete.setOnClickListener {
            deleteCurrentMedia()
        }
    }

    override fun observeData() {
        super.observeData()

        viewModel.updatedUriDataList.observe(viewLifecycleOwner) { dataList ->
            ProgressHelper.hideProgress(binding.progressBar)
            if (!dataList.isNullOrEmpty()) {
                mediaExtras =
                    mediaExtras.toBuilder().mediaUris(dataList as ArrayList<SingleUriData>).build()
                initRVForMedias()
            }
        }

        viewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    private fun initRichEditorSupport() {
        binding.etConversation.addListener(object : LikeMindsEditTextListener {
            override fun onMediaSelected(contentUri: Uri, mimeType: String) {
            }
        })
    }

    //result callback for new media pick from custom gallery
    private val pickerLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result?.resultCode == Activity.RESULT_OK && result.data != null) {
            val mediaPickerResult =
                result.data?.extras?.getParcelable<MediaPickerResult>(MediaPickerActivity.ARG_MEDIA_PICKER_RESULT)
                    ?: return@registerForActivityResult
            when (mediaPickerResult.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    val intent =
                        AndroidUtils.getExternalPickerIntent(
                            mediaPickerResult.mediaTypes,
                            mediaPickerResult.allowMultipleSelect,
                            mediaPickerResult.browseClassName
                        )
                    if (intent != null)
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

    private fun deleteCurrentMedia() {
        mediaExtras.mediaUris?.removeAt(selectedPosition)

        if (mediaExtras.mediaUris?.size == 0) {
            binding.buttonBack.performClick()
            return
        } else {
            if (selectedPosition == mediaExtras.mediaUris?.size) {
                selectedPosition -= 1
            }
            imageAdapter.replace(mediaExtras.mediaUris?.mapIndexed { index, singleMediaUri ->
                val isSelected = index == selectedPosition
                val smallMediaViewData = SmallMediaViewData.Builder().singleUriData(singleMediaUri)
                    .isSelected(isSelected).build()

                if (isSelected) {
                    selectedUri = smallMediaViewData.singleUriData()
                }
                smallMediaViewData
            })

            initMedia(selectedUri)
        }
    }

    private fun initMedia(singleUriData: SingleUriData?) {
        if (singleUriData == null)
            return

        invalidateDeleteMediaIcon(mediaExtras.mediaUris?.size)
        invalidateEditMediaIcons(singleUriData.fileType())

        when (singleUriData.fileType()) {
            VIDEO -> {
                binding.photoView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                binding.videoView
                    .setOnTrimVideoListener(object : OnTrimVideoListener {
                        override fun onVideoRangeChanged() {
                            editMode = TRIM
                        }

                        override fun onTrimStarted() {
                            ProgressHelper.showProgress(binding.progressBar)
                        }

                        override fun getResult(uri: Uri, videoTrimExtras: VideoTrimExtras?) {
                            saveTrimVideo(uri)
                            ProgressHelper.hideProgress(binding.progressBar)

                            reset()
                            replaceItem(
                                selectedPosition,
                                SingleUriData.Builder().uri(uri)
                                    .fileType(uri.getMediaType(requireContext()))
                                    .build()
                            )
                            if (videoTrimExtras?.initiatedSendMessage() == true) {
                                initSendClick()
                            } else if (videoTrimExtras?.newMediaSelected() == true) {
                                showUpdatedPositionData(
                                    videoTrimExtras.updatedMediaPosition()!!,
                                    videoTrimExtras.updatedMediaData()!!
                                )
                            }
                        }

                        override fun onFailed(videoTrimExtras: VideoTrimExtras?) {
                            ProgressHelper.hideProgress(binding.progressBar)
                            ViewUtils.showSomethingWentWrongToast(requireContext())
                        }

                        override fun cancelAction() {
                            ProgressHelper.hideProgress(binding.progressBar)
                        }

                        override fun onError(message: String) {
                            ProgressHelper.hideProgress(binding.progressBar)
                        }

                    })
                    .setVideoURI(singleUriData.uri())
                    .setVideoInformationVisibility(true)
            }
            IMAGE, GIF -> {
                binding.videoView.visibility = View.GONE
                binding.photoView.visibility = View.VISIBLE
                Glide.with(binding.photoView).load(singleUriData.uri()).into(binding.photoView)
            }
        }
    }

    private fun initRVForMedias() {
        val mediaUris = mediaExtras.mediaUris.orEmpty()
        if (mediaUris.size == 1) {
            binding.rvMedias.visibility = View.GONE
        } else {
            initMediaDisplayRecyclerView(mediaUris)
        }
        if (mediaUris.isEmpty())
            return
        selectedUri = mediaUris.firstOrNull()
        initMedia(selectedUri)
    }

    private fun initMediaDisplayRecyclerView(mediaUris: List<SingleUriData>?) {
        binding.rvMedias.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvMedias.layoutManager = linearLayoutManager

        imageAdapter = ImageAdapter(this)
        binding.rvMedias.adapter = imageAdapter
        imageAdapter.replace(mediaUris?.mapIndexed { index, singleUriData ->
            val isSelected = index == 0
            SmallMediaViewData.Builder().singleUriData(singleUriData).isSelected(isSelected)
                .build()
        })
    }

    private fun invalidateEditMediaIcons(fileType: String?) {
        binding.buttonCropRotate.isVisible = fileType != VIDEO
        binding.buttonText.isVisible = fileType != VIDEO
        binding.buttonDraw.isVisible = fileType != VIDEO
    }

    private fun invalidateDeleteMediaIcon(mediaFilesCount: Int?) {
        if (mediaFilesCount == 1) {
            binding.buttonDelete.visibility = View.GONE
        } else {
            binding.buttonDelete.visibility = View.VISIBLE
        }
    }

    override fun mediaSelected(position: Int, smallMediaViewData: SmallMediaViewData) {
        if (selectedPosition == position) return
        saveBitmapAndReset(
            false,
            VideoTrimExtras.Builder().newMediaSelected(true).updatedMediaPosition(position)
                .updatedMediaData(smallMediaViewData).build()
        )
        if (editMode != TRIM) {
            showUpdatedPositionData(position, smallMediaViewData)
        }
    }

    private fun showUpdatedPositionData(position: Int, smallMediaViewData: SmallMediaViewData) {
        selectedPosition = position
        selectedUri = smallMediaViewData.singleUriData()
        initMedia(selectedUri)
        val items = imageAdapter.items().map { it as SmallMediaViewData }
        imageAdapter.update(
            selectedPosition,
            smallMediaViewData.toBuilder().isSelected(true).build()
        )
        items.indices.map { index ->
            if (index != selectedPosition) {
                imageAdapter.update(
                    index,
                    items[index].toBuilder().isSelected(false).build()
                )
            }
        }
    }

    private fun initSendClick() {
        val text = memberTagging.replaceSelectedMembers(
            binding.etConversation.editableText
        )
        val extras = mediaExtras.toBuilder().conversation(text).build()

        if (mediaExtras.isExternallyShared) {
            viewModel.sendThirdPartySharingEvent(
                "image/video", mediaExtras.chatroomType,
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.searchKey,
                mediaExtras.chatroomId.toString()
            )
        }
        val intent = Intent()
        intent.putExtra(
            BUNDLE_MEDIA_EXTRAS,
            extras
        )
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun initMemberTagging() {
        memberTagging = binding.memberTaggingView
        memberTagging.initialize(
            MemberTaggingExtras.Builder()
                .editText(binding.etConversation)
                .color(
                    BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                        binding.etConversation.context,
                        R.color.pure_blue
                    )
                )
                .darkMode(true)
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

    //result callback for new media pick
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

    private fun updateMediaUris(mediaUris: ArrayList<SingleUriData>, saveInCache: Boolean = false) {
        if (mediaUris.isNotEmpty()) {
            if (mediaExtras.mediaUris?.size == 1) {
                initMediaDisplayRecyclerView(mediaExtras.mediaUris)
            }

            if (saveInCache) {
                mediaExtras.mediaUris?.addAll(
                    AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris.toTypedArray())
                )
            } else {
                mediaExtras.mediaUris?.addAll(mediaUris)
            }

            imageAdapter.replace(mediaExtras.mediaUris?.mapIndexed { index, singleMediaUri ->
                val isSelected = index == selectedPosition
                SmallMediaViewData.Builder().singleUriData(singleMediaUri)
                    .isSelected(isSelected)
                    .build()
            })
            invalidateDeleteMediaIcon(mediaExtras.mediaUris?.size)
        }
    }

    //callback when color is changed with the seek bar
    override fun onColorChangeListener(color: Int) {
        showBackgroundDrawable(color)
        if (binding.inputText.visibility == View.VISIBLE) {
            binding.inputText.setTextColor(color)
        }
        changeColor(color)
    }

    private fun showBackgroundDrawable(color: Int) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
        when (editMode) {
            DRAW -> binding.buttonDraw.background = drawable
            TEXT -> binding.buttonText.background = drawable
        }
    }

    private fun saveTrimVideo(uri: Uri) {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(requireContext(), uri)
        val duration = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        val width = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toLong()
        val height = mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toLong()
        val values = ContentValues()
        values.put(MediaStore.Video.Media.DATA, uri.path)
        values.put(MediaStore.Video.VideoColumns.DURATION, duration)
        values.put(MediaStore.Video.VideoColumns.WIDTH, width)
        values.put(MediaStore.Video.VideoColumns.HEIGHT, height)
        requireContext().contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values
        )?.let { ContentUris.parseId(it) }
    }

    private fun saveBitmapAndReset(send: Boolean, videoTrimExtras: VideoTrimExtras? = null) {
        if (editMode == null) return
        if (editMode == TRIM) {
            val updatedVideoTrimExtrasBuilder =
                if (videoTrimExtras != null) videoTrimExtras.toBuilder()
                else VideoTrimExtras.Builder()

            binding.videoView.onSaveClicked(
                updatedVideoTrimExtrasBuilder.initiatedSendMessage(send).build()
            )
        } else {
            binding.headerView.visibility = View.GONE
            binding.bottomView.visibility = View.GONE
            binding.buttonSend.visibility = View.GONE
            val bitmap = binding.canvas.getBitmap()
            binding.headerView.visibility = View.VISIBLE
            binding.bottomView.visibility = View.VISIBLE
            binding.buttonSend.visibility = View.VISIBLE
            val uri = FileUtil.getUriFromBitmapWithRandomName(requireContext(), bitmap)
            reset()
            replaceItem(
                selectedPosition,
                SingleUriData.Builder().uri(uri).fileType(uri.getMediaType(requireContext()))
                    .build()
            )
            if (send) {
                initSendClick()
            }
        }
    }

    private fun saveBitmapAndResetAndShowCanvas(mode: MediaEditMode) {
        if (editMode == null) return
        binding.headerView.visibility = View.GONE
        binding.bottomView.visibility = View.GONE
        binding.buttonSend.visibility = View.GONE
        val bitmap = binding.canvas.getBitmap()
        binding.headerView.visibility = View.VISIBLE
        binding.bottomView.visibility = View.VISIBLE
        binding.buttonSend.visibility = View.VISIBLE
        val uri = FileUtil.getUriFromBitmapWithRandomName(requireContext(), bitmap)
        reset()
        replaceItem(
            selectedPosition,
            SingleUriData.Builder().uri(uri).fileType(uri.getMediaType(requireContext())).build()
        )

        editMode = mode
        binding.canvas.mode = editMode!!
        binding.canvas.visibility = View.VISIBLE
        binding.photoView.visibility = View.GONE
        binding.canvas.drawBitmap(bitmap)
        changeColor(binding.colorSeekBar.getColor())

        //Scaling the canvas to avoid drawing on out of bounds area
        val width = bitmap.width
        val height = bitmap.height
        val params = binding.canvas.layoutParams
        params.width = width
        params.height = height
        binding.canvas.layoutParams = params
    }

    private fun replaceItem(position: Int, uri: SingleUriData?) {
        selectedUri = uri
        val newMediaExtras = mediaExtras
        mediaExtras.mediaUris?.let {
            for (i in 0 until it.size) {
                if (i == position) {
                    newMediaExtras.mediaUris!![position] = uri!!
                }
            }
        }
        mediaExtras = newMediaExtras
        imageAdapter = ImageAdapter(this)
        imageAdapter.replace(mediaExtras.mediaUris?.mapIndexed { index, singleUriData ->
            val isSelected = index == selectedPosition
            SmallMediaViewData.Builder().singleUriData(singleUriData).isSelected(isSelected)
                .build()
        })
        initMedia(uri)
    }

    private fun showColorChooser() {
        binding.colorSeekBar.visibility = View.VISIBLE
        binding.colorSeekBarHolder.visibility = View.VISIBLE
    }

    private fun hideColorChooser() {
        binding.colorSeekBar.visibility = View.GONE
        binding.colorSeekBarHolder.visibility = View.GONE
    }

    private fun reset() {
        ViewUtils.hideKeyboard(requireContext())
        binding.canvas.clear()
        hideColorChooser()
        editMode = null
        currentTextMode = -1
        currentTextSize = 1
        binding.buttonText.background = null
        binding.buttonDraw.background = null
        binding.buttonUndo.visibility = View.GONE
        binding.inputText.visibility = View.GONE
        binding.buttonTextIncrease.visibility = View.GONE
        binding.buttonTextDecrease.visibility = View.GONE
        binding.canvas.visibility = View.GONE
        binding.photoView.visibility = View.VISIBLE
    }

    private fun addTextToCanvas() {
        if (binding.inputText.visibility != View.VISIBLE) {
            return
        }
        ViewUtils.hideKeyboard(binding.inputText)
        val text = binding.inputText.text.toString().trim()
        if (text.isEmpty()) {
            reset()
            return
        }
        binding.canvas.textToCenter = true
        binding.canvas.text = text
        binding.inputText.text = null
        binding.inputText.visibility = View.GONE
    }

    //Showing canvas on edit on for DRAW and TEXT
    private fun showCanvas(mode: MediaEditMode) {
        val bitmap = (binding.photoView.drawable as? BitmapDrawable)?.bitmap ?: return
        editMode = mode
        binding.canvas.visibility = View.VISIBLE
        binding.photoView.visibility = View.GONE
        binding.canvas.drawBitmap(bitmap)
        binding.canvas.mode = editMode!!
        changeColor(binding.colorSeekBar.getColor())

        //Scaling the canvas to avoid drawing on out of bounds area
        val width = bitmap.width
        val height = bitmap.height
        val params = binding.canvas.layoutParams
        params.width = width
        params.height = height
        binding.canvas.layoutParams = params
    }

    private fun changeColor(color: Int) {
        binding.canvas.setPaintColor(color)
    }

    private fun changeTextMode() {
        if (currentTextMode == 3) {
            currentTextMode = 0
        } else {
            currentTextMode++
        }
        binding.buttonText.setImageResource(textIcons[currentTextMode])
        binding.canvas.setTypeface(textTypefaces[currentTextMode]!!)
        binding.inputText.typeface = textTypefaces[currentTextMode]!!
    }

    private fun changeTextSize() {
        binding.canvas.setFontSize(textSizes[currentTextSize])
        binding.inputText.textSize = textSizes[currentTextSize]
    }

    //On canvas draw start
    override fun onDrawStart() {

    }

    //On canvas draw end
    override fun onDrawEnd() {

    }

    fun onBackPressedFromFragment() {
        if (mediaExtras.isExternallyShared) {
            viewModel.sendThirdPartyAbandoned(
                "image/video",
                mediaExtras.communityId.toString(),
                mediaExtras.communityName,
                mediaExtras.chatroomId.toString()
            )
        }
    }

    override fun onUndoAvailable(undoAvailable: Boolean) {
        binding.buttonUndo.visibility = if (undoAvailable) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCanvasClick() {
        if (editMode == TEXT) {
            addTextToCanvas()
        }
    }

}