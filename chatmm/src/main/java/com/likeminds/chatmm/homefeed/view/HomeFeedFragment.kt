package com.likeminds.chatmm.homefeed.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.chatmm.InitiateViewModel
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.SDKApplication.Companion.LOG_TAG
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.model.MemberViewData
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity
import com.likeminds.chatmm.chatroom.explore.view.ChatroomExploreActivity
import com.likeminds.chatmm.databinding.FragmentHomeFeedBinding
import com.likeminds.chatmm.homefeed.model.ChatViewData
import com.likeminds.chatmm.homefeed.model.GroupChatResponse
import com.likeminds.chatmm.homefeed.model.HomeFeedExtras
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapter
import com.likeminds.chatmm.homefeed.viewmodel.HomeFeedViewModel
import com.likeminds.chatmm.search.view.SearchActivity
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.BaseFragment
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFeedFragment : BaseFragment<FragmentHomeFeedBinding, HomeFeedViewModel>(),
    HomeFeedAdapter.HomeFeedAdapterListener {

    private lateinit var extras: HomeFeedExtras
    private lateinit var homeFeedAdapter: HomeFeedAdapter

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    @Inject
    lateinit var homeFeedPreferences: HomeFeedPreferences

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    companion object {
        const val TAG = "HomeFeedFragment"
        private const val BUNDLE_HOME_FRAGMENT = "bundle of home fragment"

        private lateinit var cb: (response: GroupChatResponse?) -> Unit

        /**
         * creates a instance of fragment
         **/
        @JvmStatic
        fun getInstance(
            extras: HomeFeedExtras,
            cb: (response: GroupChatResponse?) -> Unit,
        ): HomeFeedFragment {
            this.cb = cb
            val fragment = HomeFeedFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_HOME_FRAGMENT, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<HomeFeedViewModel> {
        return HomeFeedViewModel::class.java
    }

    override fun getViewBinding(): FragmentHomeFeedBinding {
        return FragmentHomeFeedBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().homeFeedComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        extras = requireArguments().getParcelable(BUNDLE_HOME_FRAGMENT)
            ?: throw emptyExtrasException(TAG)
        isGuestUser = extras.isGuest ?: false
    }

    override fun setUpViews() {
        super.setUpViews()
        setBranding()
        initiateUser()
        initRecyclerView()
        initToolbar()
        fetchData()
    }

    override fun observeData() {
        super.observeData()

        // observes error message
        observeErrors()

        initiateViewModel.initiateUserResponse.observe(viewLifecycleOwner) { user ->
            observeInitiateUserResponse(user)
        }

        initiateViewModel.logoutResponse.observe(viewLifecycleOwner) {
            Log.d(
                LOG_TAG,
                "initiate api sdk called -> success and have not app access"
            )
            showInvalidAccess()
        }

        viewModel.homeEventsFlow.onEach { homeEvent ->
            when (homeEvent) {
                is HomeFeedViewModel.HomeEvent.UpdateChatrooms -> {
                    updateChatrooms()
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun observeErrors() {
        initiateViewModel.initiateErrorMessage.observe(viewLifecycleOwner) {
            ViewUtils.showErrorMessageToast(requireContext(), it)
        }

        viewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is HomeFeedViewModel.ErrorMessageEvent.GetChatroom -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is HomeFeedViewModel.ErrorMessageEvent.GetExploreTabCount -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    //observe user data
    private fun observeInitiateUserResponse(user: MemberViewData?) {
        initToolbar()
        viewModel.observeChatrooms(requireContext())
        viewModel.getConfig()
        viewModel.getExploreTabCount()
        viewModel.sendCommunityTabClicked(user?.communityId, user?.communityName)
        // todo: analytics
//        viewModel.sendHomeScreenOpenedEvent(LMAnalytics.Sources.SOURCE_COMMUNITY_TAB)

        if (user != null) {
            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                user.id,
                binding.memberImage,
                showRoundImage = true,
                objectKey = user.updatedAt
            )
        }
    }

    // shows invalid access error and logs out invalid user
    private fun showInvalidAccess() {
        binding.apply {
            rvHomeFeed.hide()
            layoutRemoveAccess.root.show()
            memberImage.hide()
            ivSearch.hide()
        }
    }

    private fun updateChatrooms() {
        homeFeedAdapter.setItemsViaDiffUtilForHome(
            viewModel.getHomeFeedList(requireContext())
        )
    }

    override fun onResume() {
        super.onResume()
        // todo: check ad make exposed function in data
//        if (!LikeMindsDB.isEmpty() && !sdkPreferences.getIsGuestUser()) {
        viewModel.observeChatrooms(requireContext())
//        }
    }

    private fun setBranding() {
        binding.apply {
            toolbarColor = LMBranding.getToolbarColor()
        }
    }

    private fun initiateUser() {
        initiateViewModel.initiateUser(
            requireContext(),
            extras.apiKey,
            extras.userName,
            extras.userId,
            extras.isGuest ?: false
        )
    }

    private fun initRecyclerView() {
        homeFeedAdapter = HomeFeedAdapter(sdkPreferences, this)
        binding.rvHomeFeed.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = homeFeedAdapter
            show()
        }
        binding.layoutRemoveAccess.root.hide()
    }

    private fun initToolbar() {
        binding.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            //if user is guest user hide, profile icon from toolbar
            memberImage.isVisible = !isGuestUser

            // todo:
            //get user from local db
//        viewModel.getUserFromLocalDb()

            ivSearch.setOnClickListener {
                SearchActivity.start(requireContext())
                Log.d(LOG_TAG, "search started")
            }
        }
    }

    private fun fetchData() {
        viewModel.observeChatrooms(requireContext())
    }

    override fun onChatRoomClicked(chatViewData: ChatViewData) {
        val chatroom = chatViewData.chatroom
        openChatroom(chatroom)
    }

    private fun openChatroom(chatroom: ChatroomViewData) {
        // todo:
        val extra = ChatroomDetailExtras.Builder()
//            .collabcardViewData(chatroom)
//            .chatroomId(chatroom.id)
//            .communityId(chatroom.communityId)
//            .source(ChatroomDetailFragment.SOURCE_HOME_FEED)
            .build()
        ChatroomDetailActivity.start(requireContext(), extra)
    }

    override fun homeFeedClicked() {
        // todo: Analytics
        ChatroomExploreActivity.start(requireContext())
//        viewModel.sendCommunityFeedClickedEvent(communityId, communityName)
    }
}