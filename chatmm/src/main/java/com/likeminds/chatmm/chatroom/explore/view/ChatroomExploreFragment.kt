package com.likeminds.chatmm.chatroom.explore.view

import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.explore.model.ExploreViewData
import com.likeminds.chatmm.chatroom.explore.view.adapter.ChatroomExploreAdapter
import com.likeminds.chatmm.chatroom.explore.view.adapter.ExploreClickListener
import com.likeminds.chatmm.chatroom.explore.viewmodel.ExploreViewModel
import com.likeminds.chatmm.databinding.FragmentChatroomExploreBinding
import com.likeminds.chatmm.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.chatmm.overflowmenu.view.OverflowMenuPopup
import com.likeminds.chatmm.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ValueUtils.isValidIndex
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.model.BaseViewType
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ChatroomExploreFragment :
    BaseFragment<FragmentChatroomExploreBinding, ExploreViewModel>(),
    OverflowMenuAdapterListener,
    ExploreClickListener {

    private lateinit var endlessRecyclerScrollListenerCommunities: EndlessRecyclerScrollListener

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    private val overflowMenu: OverflowMenuPopup by lazy {
        OverflowMenuPopup.create(requireContext(), this)
    }

    private val chatroomExploreAdapter: ChatroomExploreAdapter by lazy {
        ChatroomExploreAdapter(this)
    }

    //Launcher to start Activity for result
//    private val chatroomUpdateLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                result.data?.extras?.let { bundle ->
//                    val collabcardDetailResultExtras =
//                        bundle.getParcelable<CollabcardDetailResultExtras>(
//                            ARG_COLLABCARD_DETAIL_RESULT_EXTRAS
//                        )
//                    if (collabcardDetailResultExtras != null) {
//                        if (collabcardDetailResultExtras.isCollabcardDeleted == true) {
//                            deleteCollabcard(collabcardDetailResultExtras.collabcardId)
//                        } else {
//                            if (collabcardDetailResultExtras.isCollabcardMutedChanged == true
//                                || collabcardDetailResultExtras.isCollabcardFollowChanged == true
//                                || collabcardDetailResultExtras.isCollabcardNameChanged == true
//                            ) {
//                                updateCollabcardDetails(collabcardDetailResultExtras)
//                            }
//                        }
//                    }
//                }
//            }
//        }

    override fun getViewModelClass(): Class<ExploreViewModel> {
        return ExploreViewModel::class.java
    }

    override fun getViewBinding(): FragmentChatroomExploreBinding {
        return FragmentChatroomExploreBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().exploreComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        fetchExploreChatrooms()
        initToolbar()
        initExploreRecyclerView()

        binding.tvMenu.setOnClickListener {
            showOverflowMenu()
        }
        binding.ivPinned.setOnClickListener {
            showPinnedChatrooms()
        }
        binding.tvPinned.setOnClickListener {
            showUnpinnedChatrooms()
        }
    }

    override fun observeData() {
        super.observeData()

        viewModel.menuActions.observe(this) { menuItems ->
            if (!menuItems.isNullOrEmpty()) {
                overflowMenu.setItems(menuItems)
            }
        }

        viewModel.selectedOrder.observe(viewLifecycleOwner) {
            binding.tvMenu.text = it
        }

        viewModel.showPinnedIcon.observe(viewLifecycleOwner) { showPinnedIcon ->
            if (showPinnedIcon) {
                binding.ivPinned.show()
            } else {
                binding.ivPinned.hide()
            }
        }

        viewModel.dataList.observe(viewLifecycleOwner) { list ->
            ProgressHelper.hideProgress(binding.progressBar)
            if (list.isNotEmpty()) {
                chatroomExploreAdapter.addAll(list as List<BaseViewType>)
            }
        }

        //First -> ViewData and Second -> Position to be updated
        viewModel.followStatus.observe(viewLifecycleOwner) { pair ->
            binding.rvExplore.post {
                if (pair.second.isValidIndex(chatroomExploreAdapter.items())) {
                    chatroomExploreAdapter.update(
                        pair.second,
                        pair.first
                    )
                }
            }
        }

        viewModel.errorMessageFlow.onEach { response ->
            when (response) {
                is ExploreViewModel.ErrorMessageEvent.ChatroomFollow -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is ExploreViewModel.ErrorMessageEvent.ExploreFeed -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                    requireActivity().finish()
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    //fetch explore feed open explore tab is clicked
    private fun fetchExploreChatrooms() {
        viewModel.getInitialExploreFeed()
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

    //init the recycler and attach scroll listener for pagination
    private fun initExploreRecyclerView() {
        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rvExplore.layoutManager = linearLayoutManager
            rvExplore.adapter = chatroomExploreAdapter
            attachPagination(
                rvExplore,
                linearLayoutManager
            )
        }
    }

    //attach scroll listener for pagination
    private fun attachPagination(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        endlessRecyclerScrollListenerCommunities =
            object : EndlessRecyclerScrollListener(layoutManager) {
                override fun onLoadMore(currentPage: Int) {
                    if (currentPage > 0) {
                        if (viewModel.showPinnedChatroomOnly) {
                            viewModel.getExploreFeed(currentPage, true)
                        } else {
                            viewModel.getExploreFeed(currentPage)
                        }
                    }
                }
            }
        recyclerView.addOnScrollListener(endlessRecyclerScrollListenerCommunities)
    }

    /**
     * This functions clears the list in recycler view and reattach scroll listener for pagination
     * When any filter is click i.e. Newest, Recently active, Most participants, Most messages and
     * Pinned chatrooms are shown.
     **/
    private fun resetRecyclerView() {
        initExploreRecyclerView()
        chatroomExploreAdapter.clearAndNotify()
    }

    //to show the filter of Explore feed
    private fun showOverflowMenu() {
        overflowMenu.showAsDropDown(
            binding.tvMenu,
            -ViewUtils.dpToPx(16),
            -binding.tvMenu.height / 2,
            Gravity.START
        )
    }

    //shows pinned chatrooms when user clicks pin icon
    private fun showPinnedChatrooms() {
        binding.apply {
            ivPinned.hide()
            tvPinned.show()
            viewModel.setShowPinnedChatroomsOnly(true)
            resetRecyclerView()
            viewModel.getExploreFeed(1, true)
        }
    }

    //shows all the chatrooms whether unpinned or pinned
    private fun showUnpinnedChatrooms() {
        binding.apply {
            ivPinned.show()
            tvPinned.hide()
            resetRecyclerView()
            viewModel.setShowPinnedChatroomsOnly(false)
            viewModel.getExploreFeed(1)
        }
    }

    //Handles chatroom click and show chatroom detail screen
    override fun onChatroomClick(exploreViewData: ExploreViewData, position: Int) {
        // todo: implement
//        val collabcardViewData = exploreViewData.collabcardViewData ?: return
//
//        //Update External seen
//        if (exploreViewData.externalSeen == false) {
//            binding.rvExplore.post {
//                if (position >= 0 && position < collabcardExploreAdapter.itemCount) {
//                    collabcardExploreAdapter.update(
//                        position,
//                        exploreViewData.toBuilder().externalSeen(true).build()
//                    )
//                }
//            }
//        }
//
//        //Start activity
//        val intent = CollabcardDetailActivity.getIntent(
//            requireContext(), ChatroomDetailExtras.Builder()
//                .chatroomId(collabcardViewData.id())
//                .collabcardViewData(collabcardViewData)
//                .communityId(sdkPreferences.getCommunityId())
//                .source(SOURCE_COMMUNITY_FEED)
//                .build()
//        )
//        chatroomUpdateLauncher.launch(intent)
    }

    //Handles Join/Joined button clicks
    override fun onJoinClick(follow: Boolean, position: Int, exploreViewData: ExploreViewData) {
        //Check for guest flow
        if (sdkPreferences.getIsGuestUser()) {
            //User is Guest
            // todo: login callback
            activity?.finish()
        } else {
            //User is logged in

            //Check whether logged in user is creator of the chatroom
            //Don't allow creator to leave the chatroom
            if (!follow && exploreViewData.isCreator) {
                ViewUtils.showShortSnack(
                    binding.root,
                    getString(R.string.creator_cant_leave_message)
                )
                return
            }
            viewModel.followChatroom(follow, exploreViewData, position)
        }
    }

    //Handles click of filter and show list accordingly
    override fun onMenuItemClicked(menu: OverflowMenuItemViewData) {
        overflowMenu.dismiss()
        ProgressHelper.showProgress(binding.progressBar)
        resetRecyclerView()
        viewModel.setSelectedOrder(menu.title)
        fetchExploreChatrooms()
    }
}