package com.likeminds.chatmm.chatroom.detail.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.collabmates.membertagging.model.MemberTaggingExtras
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.branding.customview.edittext.LikeMindsEditTextListener
import com.likeminds.chatmm.branding.customview.edittext.LikeMindsEmojiEditText
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailResultExtras
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.SCROLL_DOWN
import com.likeminds.chatmm.chatroom.detail.util.ChatroomScrollListener
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapter
import com.likeminds.chatmm.chatroom.detail.view.adapter.ChatroomDetailAdapterListener
import com.likeminds.chatmm.chatroom.detail.viewmodel.ChatroomDetailViewModel
import com.likeminds.chatmm.conversation.model.ConversationViewData
import com.likeminds.chatmm.databinding.FragmentChatroomDetailBinding
import com.likeminds.chatmm.media.model.GIF
import com.likeminds.chatmm.media.model.IMAGE
import com.likeminds.chatmm.media.model.VIDEO
import com.likeminds.chatmm.pushnotification.NotificationUtils
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ValueUtils.getMaxCountNumberText
import com.likeminds.chatmm.utils.ValueUtils.getMediaType
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.endRevealAnimation
import com.likeminds.chatmm.utils.ViewUtils.startRevealAnimation
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.membertagging.model.TagViewData
import com.likeminds.chatmm.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.chatmm.utils.membertagging.view.MemberTaggingView
import com.vanniktech.emoji.EmojiPopup
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import javax.inject.Inject

class ChatroomDetailFragment :
    BaseFragment<FragmentChatroomDetailBinding, ChatroomDetailViewModel>(),
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

    private val chatroomId
        get() = chatroomDetailExtras.chatroomId

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
        addToCohortAndFollowChatroom()
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
     * Check whether [cohortId] and [chatroomId] is present in [extras]
     * and call api
     **/
    // todo ask if useful or not
    private fun addToCohortAndFollowChatroom() {
        try {
            val cohortId = chatroomDetailExtras.cohortId?.toIntOrNull() ?: return
            val chatroomId = chatroomDetailExtras.chatroomId

//            viewModel.addToCohortAndFollowChatroom(cohortId)
        } catch (e: Exception) {
            return
        }
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
//        initEnterClick()
//        initAttachmentClick()
//        initAttachmentsView()
//        disableAnswerPosting()
//        initReplyView()
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
            editAnswerFocusChangeListener()
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
//        Giphy.configure(requireContext(), BuildConfig.URLS_MAP[BuildConfig.GIPHY_SDK].toString())
//        val settings = GPHSettings(GridType.waterfall, GPHTheme.Light)
//        settings.mediaTypeConfig = arrayOf(GPHContentType.recents, GPHContentType.gif)
//        settings.selectedContentType = GPHContentType.gif
//        val giphyDialog = GiphyDialogFragment.newInstance(settings)
//        giphyDialog.setTargetFragment(this, REQUEST_GIFS)
//        binding.inputBox.tvGifs.setOnClickListener {
//            if (checkIfResponseAllowed()) {
//                giphyDialog.show(parentFragmentManager, "giphy_dialog")
//            }
//        }
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

    private fun editAnswerFocusChangeListener() {
        binding.inputBox.etAnswer.onFocusChangeListener =
            View.OnFocusChangeListener { view, _ ->
                return@OnFocusChangeListener onAnswerViewFocusChanged(view)
            }
    }

    private fun onAnswerViewFocusChanged(view: View) {
        if (isNotAdminInAnnouncementRoom()) {
            // todo confirm
//            showNotAdminInPurposeChatPopUp {
//                ViewUtils.hideKeyboard(view)
//            }
            return
        }
    }

    private fun isReplyViewVisible(): Boolean {
        return binding.inputBox.viewReply.clReply.visibility == View.VISIBLE
    }

    private fun isLinkViewVisible(): Boolean {
        return binding.inputBox.viewLink.clLink.visibility == View.VISIBLE
    }

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

    override fun getChatRoom(): ChatroomViewData? {
        return getChatroomViewData()
    }
}