package com.likeminds.chatmm.chatroom.detail.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.collabmates.membertagging.model.MemberTaggingExtras
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.branding.customview.edittext.LikeMindsEditTextListener
import com.likeminds.chatmm.branding.customview.edittext.LikeMindsEmojiEditText
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.model.*
import com.likeminds.chatmm.chatroom.detail.util.ChatroomScrollListener
import com.likeminds.chatmm.chatroom.detail.util.ChatroomUtil.getTypeName
import com.likeminds.chatmm.chatroom.detail.util.VoiceNoteInterface
import com.likeminds.chatmm.chatroom.detail.util.VoiceNoteUtils
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapter
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.chatroom.detail.viewmodel.ChatroomDetailViewModel
import com.likeminds.chatmm.conversation.model.AttachmentViewData
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.conversation.model.DM_CM_BECOMES_MEMBER_DISABLE
import com.likeminds.chatmm.conversation.model.DM_MEMBER_REMOVED_OR_LEFT
import com.likeminds.chatmm.databinding.FragmentChatroomDetailBinding
import com.likeminds.chatmm.media.model.*
import com.likeminds.chatmm.media.util.LMVoiceRecorder
import com.likeminds.chatmm.media.util.MediaAudioForegroundService
import com.likeminds.chatmm.media.util.MediaUtils
import com.likeminds.chatmm.media.view.MediaActivity
import com.likeminds.chatmm.media.view.MediaActivity.Companion.BUNDLE_MEDIA_EXTRAS
import com.likeminds.chatmm.media.view.MediaPickerActivity
import com.likeminds.chatmm.media.view.MediaPickerActivity.Companion.ARG_MEDIA_PICKER_RESULT
import com.likeminds.chatmm.media.view.MediaPickerActivity.Companion.BROWSE_DOCUMENT
import com.likeminds.chatmm.media.view.MediaPickerActivity.Companion.BROWSE_MEDIA
import com.likeminds.chatmm.media.view.MediaPickerActivity.Companion.PICK_CAMERA
import com.likeminds.chatmm.pushnotification.NotificationUtils
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ValueUtils.getMaxCountNumberText
import com.likeminds.chatmm.utils.ValueUtils.getMediaType
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewUtils.endRevealAnimation
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.startRevealAnimation
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.file.util.FileUtil
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.chatmm.utils.membertagging.view.MemberTaggingView
import com.likeminds.chatmm.utils.permissions.Permission
import com.likeminds.chatmm.utils.permissions.PermissionDeniedCallback
import com.likeminds.chatmm.utils.permissions.PermissionManager
import com.vanniktech.emoji.EmojiPopup
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.IOException
import javax.inject.Inject

class ChatroomDetailFragment :
    BaseFragment<FragmentChatroomDetailBinding, ChatroomDetailViewModel>(),
    VoiceNoteInterface,
    ChatroomDetailAdapterListener {

    private lateinit var chatroomDetailExtras: ChatroomDetailExtras

    private var chatroomResultExtras: ChatroomDetailResultExtras? = null

    private lateinit var emojiPopup: EmojiPopup
    private var conversationIdForEmojiReaction = ""

    private lateinit var memberTagging: MemberTaggingView

    private var isAttachmentsSheetHiding = false

    private var reportedConversationId: String = ""
    private var searchedConversationId: String = ""
    private var scrollToHighlightTitle: Boolean = false

    //-----------For scroll and Fab experience------------
    private var unSeenConversationsSet = HashSet<String>()
    private var unSeenCount = 0
    private var visibleBottomConversationIndex = -1
    //-----------For scroll and FAb experience------------

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    lateinit var chatroomDetailAdapter: ChatroomDetailAdapter
    private lateinit var chatroomScrollListener: ChatroomScrollListener

    private var cameraPath: String? = null

    //Global variable for Audio Support as Conversation
    //For handling broadcast receiver and different audio clicked
    private var mediaAudioService: MediaAudioForegroundService? = null
    private var mediaAudioServiceBound = false
    private var serviceConnection: ServiceConnection? = null
    private var localParentConversationId: String = ""
    private var localChildPosition: Int = 0

    //Variable for Voice Note
    private var singleUriDataOfVoiceNote: SingleUriData? = null
    private var showTapAndHoldToast = true
    private var motionDownHandler: Handler? = null
    private val voiceNoteUtils: VoiceNoteUtils by lazy {
        VoiceNoteUtils(requireContext(), this)
    }
    private var firstX: Float = 0f
    private var firstY: Float = 0f
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var directionOffSet: Float = 0f
    private var cancelOffSet: Float = 0f
    private var lockOffSet: Float = 0f
    private var userBehaviours = USER_NONE
    private lateinit var voiceRecorder: LMVoiceRecorder
    private var voiceNoteFilePath: String? = ""
    private var isVoiceNotePlaying = false
    private var isVoiceNoteLocked = false
    private var stopTrackingVoiceNoteAction = false
    private var isDeletingVoiceNote = false
    private var isVoiceNoteRecording = false

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
                checkMediaPickedResult(data)
            }
        }

    private val documentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
                checkMediaPickedResult(data)
            }
        }

    private val audioLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
                checkMediaPickedResult(data)
            }
        }

    private val chatroomId
        get() = chatroomDetailExtras.chatroomId

    private val communityId
        get() = getChatroomViewData()?.communityId ?: chatroomDetailExtras.communityId

    private fun isNotAdminInAnnouncementRoom(): Boolean {
        return viewModel.isNotAdminInAnnouncementRoom()
    }

    private fun getTopConversation(): ConversationViewData? {
        return viewModel.getFirstConversationFromAdapter(
            chatroomDetailAdapter.items()
        )
    }

    private fun getBottomConversation(): ConversationViewData? {
        return viewModel.getLastConversationFromAdapter(
            chatroomDetailAdapter.items()
        )
    }

    companion object {
        const val ARG_CHATROOM_DETAIL_RESULT_EXTRAS = "ARG_CHATROOM_DETAIL_RESULT_EXTRAS"
        const val CHATROOM_DETAIL_EXTRAS = "CHATROOM_DETAIL_EXTRAS"

        const val REQUEST_GIFS = 3004
    }

    override fun getViewModelClass(): Class<ChatroomDetailViewModel> {
        return ChatroomDetailViewModel::class.java
    }

    override fun getViewBinding(): FragmentChatroomDetailBinding {
        return FragmentChatroomDetailBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().chatroomDetailComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(CHATROOM_DETAIL_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        chatroomDetailExtras = requireArguments().getParcelable(CHATROOM_DETAIL_EXTRAS)!!
        isGuestUser = sdkPreferences.getIsGuestUser()
        checkForExplicitActions()
        fetchInitialData()
    }

    /**
     * Checks and do the necessities for explicit actions, such as actions from notification,
     * or other screen intents
     */
    private fun checkForExplicitActions() {
        //If this screen was opened after clicking a reported conversation by the community manager
        if (!chatroomDetailExtras.reportedConversationId.isNullOrEmpty()) {
            reportedConversationId = chatroomDetailExtras.reportedConversationId!!
        }
        if (!chatroomDetailExtras.conversationId.isNullOrEmpty()) {
            searchedConversationId = chatroomDetailExtras.conversationId!!
        }

        if (chatroomDetailExtras.scrollToExtremeTopForHighlightingTitle == true) {
            scrollToHighlightTitle = true
        }
        dismissChatRoomNotification()
    }

    /**
     * Dismiss the active notifications of this current chatroom if it is showing
     */
    private fun dismissChatRoomNotification() {
        NotificationUtils.removeConversationNotification(
            requireContext(),
            chatroomId
        )
    }

    private fun fetchInitialData() {
        // todo: fetch data
    }

    private fun getChatroomViewData(): ChatroomViewData? {
        return viewModel.getChatroomViewData()
    }

    override fun setUpViews() {
        super.setUpViews()
        initToolbar()
        setHasOptionsMenu(true)
        initView()
        initEmojiView()
        initGiphy()
        initAttachmentClick()
        initAttachmentsView()
        disableAnswerPosting()
        initReplyView()
    }

    // initializes the toolbar
    private fun initToolbar() {
        binding.apply {
            toolbarColor = LMBranding.getToolbarColor()

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            ivBack.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun initView() {
        binding.apply {
            initRecyclerView()
            initMemberTaggingView()
            isVoiceNoteSupportEnabled = viewModel.isVoiceNoteSupportEnabled()
            inputBox.etAnswer.doAfterTextChanged {
                if (it?.toString()?.trim()
                        .isNullOrEmpty() && viewModel.isVoiceNoteSupportEnabled()
                ) {
                    fabSend.hide()
                    fabMic.show()
                } else {
                    fabSend.show()
                    fabMic.hide()
                    //LinkPreview
                    viewModel.linkPreview(
                        inputBox.etAnswer.editableText.toString().trim()
                    )
                }
                if (it?.toString()?.trim().isNullOrEmpty()) {
                    viewModel.clearLinkPreview()
                }
            }

            // scroll the recyclerview up when keyboard opens
            rvChatroom.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (!isScrollable()) {
                    hideScrollBottomFab()
                }
                if (bottom < oldBottom) {
                    rvChatroom.scrollBy(0, oldBottom - bottom)
                }
                if (KeyboardVisibilityEvent.isKeyboardVisible(requireActivity())) {
                    val height =
                        rvChatroom.height - ViewUtils.dpToPx(16)
                    memberTagging.reSetMaxHeight(height)
                }
            }

            // todo:
//            initSwipeController()
            initRichEditorSupport()

            fabScrollBottom.setOnClickListener {
                hideScrollBottomFab()
                scrollToExtremeBottom()
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvChatroom.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            layoutManager = linearLayoutManager
            chatroomDetailAdapter = ChatroomDetailAdapter(this@ChatroomDetailFragment)
            adapter = chatroomDetailAdapter
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations =
                false
            attachPagination(this, linearLayoutManager)
        }
    }

    private fun initEmojiView() {
        emojiPopup = buildEmojiPopup(binding.inputBox.etAnswer)
    }

    private fun buildEmojiPopup(editText: LikeMindsEmojiEditText): EmojiPopup {
        binding.apply {
            return EmojiPopup.Builder.fromRootView(root)
                .setIconColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.brown_grey
                    )
                )
                .setSelectedIconColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
                .setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.emoji_kb_background
                    )
                )
                .setDividerColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.emoji_kb_background
                    )
                )
                .setOnEmojiPopupShownListener {
                    // todo: analytics
//                viewModel.sendReactionsClickEvent()
                }
                .setOnEmojiPopupDismissListener {
                    conversationIdForEmojiReaction = ""
                }
                .setOnEmojiClickListener { _, emojiString ->
                    if (conversationIdForEmojiReaction.isNotEmpty()) {
//                    reactedToMessage(emojiString.unicode, conversationIdForEmojiReaction, true)
                        inputBox.etAnswer.setText("")
                        emojiPopup.dismiss()
                    }
//                if (isChatroomReaction) {
//                    reactedToMessage(emojiString.unicode, chatroomId, false)
//                    binding.inputBox.etAnswer.setText("")
//                    emojiPopup.dismiss()
//                }
                }
                .build(editText)
        }
    }

    private fun initGiphy() {
        Giphy.configure(
            requireContext(),
            String(Base64.decode(InternalKeys.GIPHY_SDK, Base64.DEFAULT))
        )
        val settings = GPHSettings(GridType.waterfall, GPHTheme.Light)
        settings.mediaTypeConfig = arrayOf(GPHContentType.recents, GPHContentType.gif)
        settings.selectedContentType = GPHContentType.gif
        val giphyDialog = GiphyDialogFragment.newInstance(settings)
        giphyDialog.setTargetFragment(this, REQUEST_GIFS)
        binding.inputBox.tvGifs.setOnClickListener {
            giphyDialog.show(parentFragmentManager, "giphy_dialog")
        }
    }

    private fun initAttachmentClick() {
        binding.apply {
            inputBox.ivAttachment.setOnClickListener {
                if (!viewModel.isAudioSupportEnabled()) {
                    layoutAttachments.ivAudio.hide()
                    layoutAttachments.tvAudioTitle.hide()
                }
                initVisibilityOfAttachmentsBar(View.VISIBLE)
            }
        }
    }

    private fun initAttachmentsView() {
        binding.layoutAttachments.apply {
            ivGallery.setOnClickListener {
                initVisibilityOfAttachmentsBar(View.GONE)
                onScreenChanged()
                val extras = MediaPickerExtras.Builder()
                    .senderName(viewModel.chatroomDetail.chatroom?.header)
                    .mediaTypes(listOf(IMAGE, VIDEO))
                    .build()

                val intent = MediaPickerActivity.getIntent(requireContext(), extras)
                galleryLauncher.launch(intent)
            }

            ivDocument.setOnClickListener {
                initVisibilityOfAttachmentsBar(View.GONE)
                onScreenChanged()
                val extra = MediaPickerExtras.Builder()
                    .senderName(viewModel.chatroomDetail.chatroom?.header)
                    .mediaTypes(listOf(PDF))
                    .build()
                val intent = MediaPickerActivity.getIntent(requireContext(), extra)
                documentLauncher.launch(intent)
            }

            ivAudio.setOnClickListener {
                initVisibilityOfAttachmentsBar(View.GONE)
                onScreenChanged()
                val extra = MediaPickerExtras.Builder()
                    .senderName(viewModel.chatroomDetail.chatroom?.header)
                    .mediaTypes(listOf(AUDIO))
                    .build()
                val intent = MediaPickerActivity.getIntent(requireContext(), extra)
                audioLauncher.launch(intent)
            }

            ivCamera.setOnClickListener {
                PermissionManager.performTaskWithPermission(
                    activity as BaseAppCompatActivity,
                    { initCameraAttachment() },
                    Permission.getCameraPermissionData(),
                    showInitialPopup = true,
                    showDeniedPopup = true,
                    permissionDeniedCallback = object : PermissionDeniedCallback {
                        override fun onDeny() {}

                        override fun onCancel() {}
                    }
                )
            }

            ivPoll.isVisible = viewModel.isMicroPollsEnabled()
            tvPollTitle.isVisible = viewModel.isMicroPollsEnabled()
            ivPoll.setOnClickListener {
                initVisibilityOfAttachmentsBar(View.GONE)
                // todo: polls
//                CreateConversationPollDialog.show(
//                    childFragmentManager,
//                    getChatroomViewData(),
//                    chatroomDetailExtras
//                )
            }
            clBottomBar.setOnClickListener {
                initVisibilityOfAttachmentsBar(View.GONE)
            }
        }
    }

    private fun disableAnswerPosting() {
        editAnswerEnableState(false)
    }

    private fun enableAnswerPosting() {
        editAnswerEnableState(true)
    }

    private fun editAnswerEnableState(state: Boolean) {
        binding.apply {
            fabSend.isEnabled = state
            inputBox.ivAttachment.isEnabled = state
            inputBox.etAnswer.isEnabled = state
            inputBox.etAnswer.setHint(R.string.type_your_response)
        }
    }

    private fun initReplyView() {
        binding.inputBox.viewReply.ivReplyClose.setOnClickListener {
            if (isEditConversationViewVisible()) {
                clearEditTextAnswer()
            }
            setChatInputBoxViewType(CHAT_BOX_NORMAL)
        }
    }

    private fun isEditConversationViewVisible(): Boolean {
        if (isReplyViewVisible()) {
            val chatReplyData = binding.inputBox.viewReply.chatReplyData
            return chatReplyData?.isEditMessage == true
        }
        return false
    }

    private fun clearEditTextAnswer() {
        binding.inputBox.etAnswer.apply {
            setText("")
            requestFocus()
            viewModel.clearLinkPreview()
        }
    }

    /**
     * Use to set different input box type, See [ChatBoxType] for all different input views
     * @param type of [ChatBoxType]
     */
    private fun setChatInputBoxViewType(
        @ChatBoxType type: Int? = null,
        lastConversationState: Int? = null,
    ) {
        binding.apply {
            if (!viewModel.hasMemberRespondRight()) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text =
                    getString(R.string.the_community_managers_have_restricted_you_from_responding_here)
                return
            }
            if (!viewModel.isAdminMember() && viewModel.isAnnouncementChatroom()) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text =
                    getString(R.string.only_community_managers_can_respond)
                textViewRestrictedMessage.gravity = Gravity.CENTER
                return
            }
            if (!viewModel.isAdminMember() && viewModel.canMembersCanMessage() == false) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text =
                    getString(R.string.only_community_managers_can_respond)
                textViewRestrictedMessage.gravity = Gravity.CENTER
                return
            }
            if (isSecretChatRoom() && getChatRoom()?.followStatus != true) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text =
                    getString(R.string.secret_chatroom_restricted_message)
                return
            }
            if (lastConversationState == DM_CM_BECOMES_MEMBER_DISABLE) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text =
                    getString(R.string.direct_messages_community_manager_removed)
                return
            }
            if (lastConversationState == DM_MEMBER_REMOVED_OR_LEFT) {
                hideAllChatBoxViews()
                textViewRestrictedMessage.visibility = View.VISIBLE
                textViewRestrictedMessage.text = getString(R.string.direct_messaging_member_left)
                return
            }
            if (type == null) {
                return
            }
            inputBox.clChatContainer.visibility = View.VISIBLE
            textViewRestrictedMessage.visibility = View.GONE
            when (type) {
                CHAT_BOX_NORMAL -> {
                    if (!memberTagging.isShowing) {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_24_black10_1)
                    }
                    if (!isVoiceNoteLocked && !isVoiceNoteRecording) {
                        inputBox.ivAttachment.visibility = View.VISIBLE
                    }
                    inputBox.viewLink.clLink.visibility = View.GONE
                    inputBox.viewReply.clReply.visibility = View.GONE
                }

                CHAT_BOX_REPLY -> {
                    if (!memberTagging.isShowing) {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_12top_24_bottom_black10_1)
                    }
                    if (!isVoiceNoteLocked && !isVoiceNoteRecording) {
                        inputBox.ivAttachment.visibility = View.VISIBLE
                    }
                    inputBox.viewLink.clLink.visibility = View.GONE
                    inputBox.viewReply.clReply.visibility = View.VISIBLE
                }

                CHAT_BOX_LINK -> {
                    if (!memberTagging.isShowing) {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_12top_24_bottom_black10_1)
                    }
                    inputBox.ivAttachment.visibility = View.INVISIBLE
                    inputBox.viewLink.clLink.visibility = View.VISIBLE
                    inputBox.viewReply.clReply.visibility = View.GONE
                }

                CHAT_BOX_INTERNAL_LINK -> {
                    if (!memberTagging.isShowing) {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_12top_24_bottom_black10_1)
                    }
                    fabSend.show()
                    inputBox.ivAttachment.visibility = View.INVISIBLE
                    inputBox.viewLink.clLink.visibility = View.GONE
                    inputBox.viewReply.clReply.visibility = View.GONE
                }
            }
        }
    }

    private fun hideAllChatBoxViews() {
        binding.apply {
            inputBox.clChatContainer.visibility = View.GONE
            fabSend.visibility = View.GONE
            fabMic.visibility = View.GONE
        }
    }

    /**
     * function triggers after Camera permission is allowed.
     * Also, function start a intent to launch default camera of the device
     **/
    private fun initCameraAttachment() {
        initVisibilityOfAttachmentsBar(View.GONE)
        onScreenChanged()
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager).also {
                val cameraFile = try {
                    val file = FileUtil.createImageFile(requireContext())
                    cameraPath = file.absolutePath
                    file
                } catch (ex: IOException) {
                    Log.e("errorCreateFile", "errorCreateFile", ex)
                    null
                }
                if (cameraFile == null) {
                    ViewUtils.showShortToast(requireContext(), getString(R.string.image_not_found))
                } else {
                    try {
                        val photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            FileUtil.getFileProviderPackage(requireContext()),
                            cameraFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(
                            takePictureIntent,
                            PICK_CAMERA
                        )
                    } catch (e: Exception) {
                        ViewUtils.showShortToast(requireContext(), "Image not found")
                        Log.e(SDKApplication.LOG_TAG, "provider not found, ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    private fun callGuestFlowCallback() {
        SDKApplication.getLikeMindsCallback()?.login()
        activity?.finish()
    }

    private fun initMemberTaggingView() {
        binding.apply {
            memberTagging = memberTaggingView
            memberTagging.initialize(
                MemberTaggingExtras.Builder()
                    .editText(binding.inputBox.etAnswer)
                    .maxHeightInPercentage(0.4f)
                    .color(LMBranding.getTextLinkColor())
                    .build()
            )
            memberTagging.addListener(object : MemberTaggingViewListener {
                override fun onMemberTagged(user: TagViewData) {
                    // todo: analytics
//                viewModel.sendUserTagEvent(
//                    user,
//                    communityId
//                )
                }

                override fun onShow() {
                    inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_24_bottom_black10_1)
                }

                override fun onHide() {
                    if (isReplyViewVisible() || isLinkViewVisible()) {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_12top_24_bottom_black10_1)
                    } else {
                        inputBox.clChatContainer.setBackgroundResource(R.drawable.background_white_24_black10_1)
                    }
                }

                override fun callApi(page: Int, searchName: String) {
                    viewModel.getMembersForTagging(
                        chatroomId,
                        page,
                        searchName
                    )
                }
            })
        }
    }

    private fun isReplyViewVisible(): Boolean {
        return binding.inputBox.viewReply.clReply.visibility == View.VISIBLE
    }

    private fun isLinkViewVisible(): Boolean {
        return binding.inputBox.viewLink.clLink.visibility == View.VISIBLE
    }

    private fun isSecretChatRoom() = getChatroomViewData()?.isSecret == true

    /**
     * Attach scroll listener to the recyclerview for events
     */
    private fun attachPagination(
        recyclerView: RecyclerView,
        linearLayoutManager: LinearLayoutManager
    ) {
        chatroomScrollListener = object : ChatroomScrollListener(linearLayoutManager) {
            override fun onScroll() {
                // todo:
//                if (messageReactionsTray?.isShowing == true) {
//                    messageReactionsTray?.dismiss()
//                }
            }

            override fun onLoadMore(scrollState: Int) {
                // todo:
//                fetchPaginatedConversations(scrollState)
            }

            override fun onBottomReached() {
                hideScrollBottomFab()
            }

            override fun onScrollingToTop() {
                showScrollBottomFab(null)
            }

            override fun onScrollingToBottom(lastItemPosition: Int) {
                updateFabOnScrollingToBottom(lastItemPosition)
            }

            override fun onChatRoomVisibilityChanged(show: Boolean) {
                if (show) {
                    fadeInTopChatroomView()
                } else {
                    fadeOutTopChatroomView()
                }
            }
        }
        recyclerView.addOnScrollListener(chatroomScrollListener)
    }

    /**
     * Check if the recyclerview can scroll up or down
     */
    private fun isScrollable() = binding.rvChatroom.canScrollVertically(1)
            || binding.rvChatroom.canScrollVertically(-1)

    /**
     * Hide the scroll counter floating action button
     */
    private fun hideScrollBottomFab() {
        binding.apply {
            unSeenConversationsSet.clear()
            unSeenCount = 0
            fabScrollBottom.hide(object :
                FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    tvScrollBottom.visibility = View.GONE
                }
            })
        }
    }

    /**
     * Update the scroll counter floating action button when the user is scrolling down
     * @param position Position of the last completely visible item position
     */
    private fun updateFabOnScrollingToBottom(position: Int) {
        if (unSeenConversationsSet.size <= 0 || visibleBottomConversationIndex == position) {
            return
        }
        visibleBottomConversationIndex = position
        val item = chatroomDetailAdapter[position]
        if (item is ConversationViewData && unSeenConversationsSet.contains(item.id)) {
            unSeenConversationsSet.remove(item.id)
            unSeenCount--
            if (unSeenCount <= 0) {
                return
            }
            showScrollBottomFab(unSeenCount.getMaxCountNumberText())
        }
    }

    /**
     * Show the scroll counter floating action button, which triggers only if the recyclerview is scrollable
     * @param text Text to show on the counter, eg - 99+, 8, etc
     */
    private fun showScrollBottomFab(text: String?) {
        binding.fabScrollBottom.apply {
            if (!isScrollable()) {
                return
            }
            if (text == null) {
                show()
            } else {
                if (isOrWillBeShown) {
                    configureScrollBottomFab(text)
                } else {
                    show(
                        object : FloatingActionButton.OnVisibilityChangedListener() {
                            override fun onShown(fab: FloatingActionButton?) {
                                super.onShown(fab)
                                configureScrollBottomFab(text)
                            }
                        })
                }
            }
        }
    }

    /**
     * Configure the scroll counter floating action button based on the text length and visibility
     * @param text Text to show on the counter, eg - 99+, 8, etc
     */
    private fun configureScrollBottomFab(text: String) {
        binding.tvScrollBottom.apply {
            if (text.length > 2) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            } else {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            }
            this.text = text
            visibility = View.VISIBLE
        }
    }

    private fun fadeInTopChatroomView() {
        binding.apply {
            if (viewModel.isDmChatroom()) {
                return
            }
            fadeInTopChatroomEachView(viewTopBackground)
            fadeInTopChatroomEachView(memberImage)
            fadeInTopChatroomEachView(tvChatroomMemberName)
            fadeInTopChatroomEachView(tvChatroom)
            val topic = getChatRoom()?.topic
            if (topic == null) {
                fadeInTopChatroomEachView(ivDateDot)
                fadeInTopChatroomEachView(tvChatroomDate)
            } else {
                when {
                    topic.isDeleted() -> {
                        fadeInTopChatroomEachView(ivDateDot)
                        fadeInTopChatroomEachView(tvChatroomDate)
                    }

                    topic.ogTags != null -> {
                        if (topic.ogTags.image != null) {
                            fadeInTopChatroomEachView(topicImage)
                        }
                    }

                    topic.attachmentCount!! > 0 -> {
                        when (topic.attachments?.firstOrNull()?.type) {
                            IMAGE -> {
                                fadeInTopChatroomEachView(binding.topicImage)
                            }

                            VIDEO -> {
                                fadeInTopChatroomEachView(binding.topicImage)
                            }

                            GIF -> {
                                fadeInTopChatroomEachView(binding.topicImage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fadeInTopChatroomEachView(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            view.alpha = 0.0f
            view.animate()
                .setDuration(500)
                .alpha(1.0f)
                .setListener(null)
        }
    }

    private fun fadeOutTopChatroomView() {
        binding.apply {
            fadeOutTopChatroomEachView(viewTopBackground)
            fadeOutTopChatroomEachView(memberImage)
            fadeOutTopChatroomEachView(tvChatroomMemberName)
            fadeOutTopChatroomEachView(ivDateDot)
            fadeOutTopChatroomEachView(tvChatroomDate)
            fadeOutTopChatroomEachView(tvChatroom)
            fadeOutTopChatroomEachView(topicImage)
        }
    }

    private fun fadeOutTopChatroomEachView(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.animate()
                .setDuration(500)
                .alpha(0.0f)
                .setListener(fadeOutAnimatorListener(view))
        }
    }

    private fun fadeOutAnimatorListener(view: View): Animator.AnimatorListener {
        return object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
            }
        }
    }

    private fun initRichEditorSupport() {
        binding.inputBox.etAnswer.addListener(object : LikeMindsEditTextListener {
            override fun onMediaSelected(contentUri: Uri, mimeType: String) {
                mimeType.getMediaType()?.let { openMedia(contentUri, it) }
            }
        })
    }

    // todo:
    private fun openMedia(contentUri: Uri, mimeType: String) {
//        val singleUri = SingleUriData.builder()
//            .uri(contentUri)
//            .fileType(mimeType)
//            .build()
//        when {
//            viewModel.isGifSupportEnabled() && singleUri.fileType() == GIF -> {
//                showGifEditScreen(singleUri)
//            }
//
//            singleUri.fileType() == IMAGE -> {
//                showPickImagesListScreen(singleUri)
//            }
//        }
    }

    /**
     * Scroll to the bottom of the chatroom
     * Check if all bottom conversations are already added or else add it
     */
    private fun scrollToExtremeBottom() {
        val bottomConversation = getBottomConversation()
        if (bottomConversation == null) {
            scrollToPosition(SCROLL_DOWN)
        } else {
            if (viewModel.isAllBottomConversationsAdded(bottomConversation)) {
                scrollToPosition(SCROLL_DOWN)
            } else {
                chatroomScrollListener.topLoadingDone()
                // todo
//                viewModel.fetchBottomConversationsOnClick(
//                    bottomConversation,
//                    chatroomDetailAdapter.items()
//                )
            }
        }
    }

    override fun onStop() {
        setLastSeenTrueAndSaveDraftResponse()
        super.onStop()
    }

    private fun setLastSeenTrueAndSaveDraftResponse() {
        // todo:
    }

    fun consumeTouch(): Boolean {
        when {
            memberTagging.isShowing -> {
                memberTagging.hide()
                return true
            }

            isAttachmentsBarDismissing() -> return true

            isAttachmentsBarVisible() -> {
                initVisibilityOfAttachmentsBar(View.GONE)
                return true
            }
        }
        return false
    }

    private fun isAttachmentsBarDismissing(): Boolean {
        return isAttachmentsSheetHiding
    }

    private fun isAttachmentsBarVisible(): Boolean {
        return binding.layoutAttachments.clBottomBar.visibility == View.VISIBLE
    }

    private fun initVisibilityOfAttachmentsBar(visibility: Int) {
        binding.layoutAttachments.apply {
            if (visibility == View.VISIBLE) {
                root.startRevealAnimation(binding.inputBox.ivAttachment)
            } else {
                isAttachmentsSheetHiding = true
                root.endRevealAnimation(binding.inputBox.ivAttachment) {
                    isAttachmentsSheetHiding = false
                }
            }
        }
    }

    /**
     * Scroll to a position on the recyclerview
     * @param pos Index of the item to scroll to
     */
    private fun scrollToPosition(pos: Int) {
        binding.rvChatroom.apply {
            if (pos.isValidIndex(chatroomDetailAdapter.itemCount)) {
                scrollToPosition(pos)
            } else {
                hideScrollBottomFab()
                scrollToPosition(chatroomDetailAdapter.itemCount - 1)
            }
        }
    }

    fun setChatroomDetailActivityResult() {
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putParcelable(ARG_CHATROOM_DETAIL_RESULT_EXTRAS, chatroomResultExtras)
            })
        }
        activity?.setResult(Activity.RESULT_OK, intent)
    }

    private fun stopAudioService() {
        mediaAudioServiceBound = false
        mediaAudioService?.stopMedia()
        mediaAudioService?.stopSelf()
        mediaAudioService?.stopForeground(true)
        mediaAudioService?.removeHandler()
        requireActivity().unbindService(serviceConnection!!)
    }

    private fun updateAudioVoiceNoteBinder(
        data: AttachmentViewData,
        parentPositionId: String,
        childPosition: Int,
    ) {
        if (data.type == AUDIO) {
            updateAudioConversationList(data, parentPositionId, childPosition)
        } else {
            updateVoiceNote(data, parentPositionId, childPosition)
        }
    }

    private fun updateAudioConversationList(
        data: AttachmentViewData,
        parentPositionId: String,
        childPosition: Int,
    ) {
        val parentPosition = chatroomDetailAdapter.items()
            .indexOfFirst { ((it is ConversationViewData) && (it.id == parentPositionId)) }

        if (parentPosition.isValidIndex()) {
            val item =
                chatroomDetailAdapter.items()[parentPosition] as? ConversationViewData
                    ?: return

            item.attachments?.set(childPosition, data)

            chatroomDetailAdapter.items()[parentPosition] = item
        }

        // todo: conversation
//        val itemConversationAudioBinding =
//            (binding.rvChatroom.findViewHolderForAdapterPosition(
//                parentPosition
//            ) as? DataBoundViewHolder<*>)?.binding as? ItemConversationAudioBinding
//                ?: return
//        val itemAudioBinding = (itemConversationAudioBinding.audioView.binding.recyclerView
//            .findViewHolderForAdapterPosition(childPosition) as? DataBoundViewHolder<*>)
//            ?.binding as? ItemAudioBinding ?: return
//
//        itemAudioBinding.attachment = data
//        ChatroomConversationItemViewDataBinderUtil.initAudioItemView(itemAudioBinding, data)
    }

    private fun updateVoiceNote(
        data: AttachmentViewData,
        parentPositionId: String,
        childPosition: Int,
    ) {
        val parentPosition = chatroomDetailAdapter.items()
            .indexOfFirst { ((it is ConversationViewData) && (it.id == parentPositionId)) }

        if (parentPosition.isValidIndex()) {
            val item =
                chatroomDetailAdapter.items()[parentPosition] as? ConversationViewData
                    ?: return
            item.attachments?.set(childPosition, data)

            chatroomDetailAdapter.update(parentPosition, item)
        }
    }

    override fun getChatRoom(): ChatroomViewData? {
        return getChatroomViewData()
    }

    //add this function for every navigation from chatroom
    override fun onScreenChanged() {
        if (isVoiceNoteLocked) {
            isVoiceNoteLocked = false
            voiceNoteUtils.stopVoiceNote(binding, RECORDING_LOCK_DONE)
        }

        if (mediaAudioServiceBound && mediaAudioService?.isPlaying() == true) {
            stopAudioService()

            val item =
                chatroomDetailAdapter.items().firstOrNull {
                    ((it is ConversationViewData) && (it.id == localParentConversationId))
                } as? ConversationViewData

            var attachment = item?.attachments?.get(localChildPosition) ?: return

            attachment = attachment.toBuilder()
                .progress(0)
                .mediaState(MEDIA_ACTION_NONE)
                .currentDuration(requireContext().getString(R.string.start_duration))
                .build()

            updateAudioVoiceNoteBinder(
                attachment,
                localParentConversationId,
                localChildPosition
            )
        }
    }

    private fun postConversationWithMedia(mediaExtras: MediaExtras?) {
        // todo: in conversation
//        postConversation(
//            conversation = mediaExtras?.conversation?.trim() ?: "",
//            fileUris = mediaExtras?.mediaUris
//        )
    }

    private fun checkMediaPickedResult(result: MediaPickerResult?) {
        if (result != null) {
            when (result.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    if (InternalMediaType.isPDF(result.mediaTypes)) {
                        val intent = AndroidUtils.getExternalDocumentPickerIntent(
                            allowMultipleSelect = result.allowMultipleSelect
                        )
                        startActivityForResult(intent, BROWSE_DOCUMENT)
                    } else {
                        val intent = AndroidUtils.getExternalPickerIntent(
                            result.mediaTypes,
                            result.allowMultipleSelect,
                            result.browseClassName
                        )
                        if (intent != null)
                            startActivityForResult(intent, BROWSE_MEDIA)
                    }
                }

                MEDIA_RESULT_PICKED -> {
                    onMediaPicked(result)
                }
            }
        }
    }

    private fun onMediaPicked(result: MediaPickerResult) {
        val data =
            MediaUtils.convertMediaViewDataToSingleUriData(requireContext(), result.medias)
        if (data.isNotEmpty()) {
            when {
                InternalMediaType.isPDF(result.mediaTypes) -> {
                    showPickDocumentsListScreen(*data.toTypedArray())
                }

                InternalMediaType.isAudio(result.mediaTypes) -> {
                    showPickAudioListScreen(*data.toTypedArray())
                }

                else -> {
                    showPickImagesListScreen(*data.toTypedArray())
                }
            }
        }
    }

    private var documentSendLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras?.getParcelable<MediaExtras>(BUNDLE_MEDIA_EXTRAS)
                    ?: return@registerForActivityResult
                postConversationWithMedia(data)
            } else if (result?.resultCode == Activity.RESULT_FIRST_USER) {
                activity?.finish()
            }
        }

    private fun showPickDocumentsListScreen(
        vararg mediaUris: SingleUriData,
        saveInCache: Boolean = false,
        isExternallyShared: Boolean = false,
        textAlreadyPresent: String? = null,
    ) {
        val attachments = if (saveInCache) {
            AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris)
        } else {
            mediaUris.asList()
        }
        val text = if (!textAlreadyPresent.isNullOrEmpty()) {
            textAlreadyPresent
        } else {
            memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
        }

        val arrayList = ArrayList<SingleUriData>()
        arrayList.addAll(attachments)

        val mediaExtras = MediaExtras.Builder()
            .mediaScreenType(MEDIA_DOCUMENT_SEND_SCREEN)
            .mediaUris(arrayList)
            .chatroomId(chatroomId)
            .chatroomName(getChatroomViewData()?.header)
            .communityName(getChatroomViewData()?.communityName)
            .chatroomType(getChatroomViewData()?.getTypeName())
            .searchKey(chatroomDetailExtras.searchKey)
            .communityId(communityId?.toIntOrNull())
            .text(text)
            .isExternallyShared(isExternallyShared)
            .isSecretChatroom(getChatroomViewData()?.isSecret)
            .build()
        if (attachments.isNotEmpty()) {
            val intent =
                MediaActivity.getIntent(requireContext(), mediaExtras, activity?.intent?.clipData)
            documentSendLauncher.launch(intent)
        }
    }

    private val audioSendLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras?.getParcelable<MediaExtras>(BUNDLE_MEDIA_EXTRAS)
                    ?: return@registerForActivityResult
                postConversationWithMedia(data)
            } else if (result?.resultCode == Activity.RESULT_FIRST_USER) {
                activity?.finish()
            }
        }

    private fun showPickAudioListScreen(
        vararg mediaUris: SingleUriData,
        saveInCache: Boolean = false,
        isExternallyShared: Boolean = false,
        textAlreadyPresent: String? = null,
    ) {
        val attachments = if (saveInCache) {
            AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris)
        } else {
            mediaUris.asList()
        }

        if (attachments.isNotEmpty()) {
            val text = if (!textAlreadyPresent.isNullOrEmpty()) {
                textAlreadyPresent
            } else {
                memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
            }

            val arrayList = ArrayList<SingleUriData>()
            arrayList.addAll(attachments)

            val mediaExtras = MediaExtras.Builder()
                .mediaScreenType(MEDIA_AUDIO_EDIT_SEND_SCREEN)
                .mediaUris(arrayList)
                .chatroomId(chatroomId)
                .chatroomName(getChatroomViewData()?.header)
                .communityId(communityId?.toIntOrNull())
                .communityName(getChatroomViewData()?.communityName)
                .chatroomType(getChatroomViewData()?.getTypeName())
                .searchKey(chatroomDetailExtras.searchKey)
                .text(text)
                .isSecretChatroom(getChatroomViewData()?.isSecret)
                .isExternallyShared(isExternallyShared)
                .build()
            if (attachments.isNotEmpty()) {
                val intent = MediaActivity.getIntent(
                    requireContext(),
                    mediaExtras,
                    activity?.intent?.clipData
                )
                audioSendLauncher.launch(intent)
            }
        }
    }

    private var imageVideoSendLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.extras?.getParcelable<MediaExtras>(BUNDLE_MEDIA_EXTRAS)
                    ?: return@registerForActivityResult
                postConversationWithMedia(data)
            } else if (result?.resultCode == Activity.RESULT_FIRST_USER) {
                activity?.finish()
            }
        }

    private fun showGifEditScreen(singleUriData: SingleUriData) {
        val attachments = AndroidUtils.moveAttachmentToCache(requireContext(), singleUriData)
        if (attachments.isEmpty()) return

        onScreenChanged()
        val arrayList = ArrayList<SingleUriData>()
        arrayList.addAll(attachments)
        val text = memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
        val mediaExtras = MediaExtras.Builder()
            .mediaScreenType(MEDIA_GIF_SEND_SCREEN)
            .mediaUris(arrayList)
            .chatroomId(chatroomId)
            .communityId(communityId?.toIntOrNull())
            .text(text)
            .build()
        val intent =
            MediaActivity.getIntent(requireContext(), mediaExtras, activity?.intent?.clipData)
        imageVideoSendLauncher.launch(intent)
    }

    private fun showPickImagesListScreen(
        vararg mediaUris: SingleUriData,
        saveInCache: Boolean = false,
        isExternallyShared: Boolean = false,
        textAlreadyPresent: String? = null,
    ) {
        val attachments = if (saveInCache) {
            AndroidUtils.moveAttachmentToCache(requireContext(), *mediaUris)
        } else {
            mediaUris.asList()
        }
        if (attachments.isNotEmpty()) {
            val text = if (!textAlreadyPresent.isNullOrEmpty()) {
                textAlreadyPresent
            } else {
                memberTagging.replaceSelectedMembers(binding.inputBox.etAnswer.editableText)
            }

            val arrayList = ArrayList<SingleUriData>()
            arrayList.addAll(attachments)

            val mediaExtras = MediaExtras.Builder()
                .mediaScreenType(MEDIA_CONVERSATION_EDIT_SCREEN)
                .mediaUris(arrayList)
                .chatroomId(chatroomId)
                .chatroomName(getChatroomViewData()?.header)
                .communityName(getChatroomViewData()?.communityName)
                .chatroomType(getChatroomViewData()?.getTypeName())
                .searchKey(chatroomDetailExtras.searchKey)
                .communityId(communityId?.toIntOrNull())
                .text(text)
                .isExternallyShared(isExternallyShared)
                .isSecretChatroom(getChatroomViewData()?.isSecret)
                .build()

            val intent =
                MediaActivity.getIntent(requireContext(), mediaExtras, activity?.intent?.clipData)
            imageVideoSendLauncher.launch(intent)
        }
    }

    /**------------------------------------------------------------
     * Voice note listeners
    ---------------------------------------------------------------*/

    override fun onVoiceNoteStarted() {
        if (mediaAudioService?.isPlaying() == true) {
            val item =
                chatroomDetailAdapter.items().firstOrNull {
                    ((it is ConversationViewData) && (it.id == localParentConversationId))
                } as? ConversationViewData ?: return

            if ((item.attachmentCount ?: 0) >= 1) {
                var attachment = item.attachments?.get(localChildPosition) ?: return

                attachment = attachment.toBuilder()
                    .mediaState(MEDIA_ACTION_PLAY)
                    .build()

                // todo: conversation
//                onAudioConversationActionClicked(
//                    attachment,
//                    localParentConversationId,
//                    localChildPosition,
//                    0
//                )
            }
        }

        try {
            isVoiceNotePlaying = false
            voiceNoteFilePath =
                "${requireContext().externalCacheDir?.absolutePath}/VOC_${System.currentTimeMillis()}.aac"
            voiceRecorder.startRecording(voiceNoteFilePath ?: "")
        } catch (e: IllegalStateException) {
            voiceNoteUtils.stopVoiceNote(binding, RECORDING_RELEASED)
        }
    }

    override fun onVoiceNoteLocked() {
        binding.fabMic.hide()
        binding.fabSend.show()
    }

    override fun onVoiceNoteCompleted() {
        binding.apply {
            fabMic.hide()
            fabSend.show()
            mediaAudioService?.isDataSourceSet = false
            singleUriDataOfVoiceNote = if (voiceRecorder.isRecording()) {
                voiceRecorder.stopRecording(requireContext())
            } else {
                null
            }
            inputBox.tvVoiceNoteTime.text =
                DateUtil.formatSeconds(singleUriDataOfVoiceNote?.duration ?: 0)
        }
    }

    override fun onVoiceNoteCancelled() {
        binding.apply {
            isVoiceNoteRecording = false
            isVoiceNotePlaying = false
            fabMic.show()
            fabSend.hide()
            inputBox.ivPlayRecording.setImageResource(R.drawable.ic_voice_play)
            singleUriDataOfVoiceNote = null
            if (voiceRecorder.isRecording()) {
                voiceRecorder.stopRecording(requireContext())
            }
        }
    }

    override fun onVoiceNoteSend() {
        binding.apply {
            isVoiceNoteRecording = false
            fabMic.show()
            inputBox.ivPlayRecording.setImageResource(R.drawable.ic_voice_play)
            fabSend.hide()
            isVoiceNotePlaying = false
            singleUriDataOfVoiceNote = null
        }
    }

    override fun isVoiceNoteLocked(): Boolean {
        return isVoiceNoteLocked
    }

    override fun isDeletingVoiceNote(value: Boolean) {
        isDeletingVoiceNote = value
    }

    override fun stopTrackingVoiceNoteAction(value: Boolean) {
        stopTrackingVoiceNoteAction = value
    }
}