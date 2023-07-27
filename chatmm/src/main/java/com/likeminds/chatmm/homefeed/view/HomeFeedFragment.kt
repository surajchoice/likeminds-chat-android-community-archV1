package com.likeminds.chatmm.homefeed.view

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.chatmm.*
import com.likeminds.chatmm.SDKApplication.Companion.LOG_TAG
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import com.likeminds.chatmm.chatroom.explore.view.ChatroomExploreActivity
import com.likeminds.chatmm.databinding.FragmentHomeFeedBinding
import com.likeminds.chatmm.homefeed.model.HomeFeedExtras
import com.likeminds.chatmm.homefeed.model.HomeFeedItemViewData
import com.likeminds.chatmm.homefeed.util.HomeFeedPreferences
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapter
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapterListener
import com.likeminds.chatmm.homefeed.viewmodel.HomeFeedViewModel
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.member.util.UserPreferences
import com.likeminds.chatmm.pushnotification.viewmodel.LMNotificationViewModel
import com.likeminds.chatmm.search.view.SearchActivity
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.connectivity.ConnectivityReceiverListener
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.observeInLifecycle
import com.likeminds.chatmm.utils.snackbar.CustomSnackBar
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFeedFragment : BaseFragment<FragmentHomeFeedBinding, HomeFeedViewModel>(),
    HomeFeedAdapterListener,
    ConnectivityReceiverListener {

    private lateinit var extras: HomeFeedExtras
    private lateinit var homeFeedAdapter: HomeFeedAdapter

    @Inject
    lateinit var snackBar: CustomSnackBar

    @Inject
    lateinit var lmNotificationViewModel: LMNotificationViewModel

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var homeFeedPreferences: HomeFeedPreferences

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    private var wasNetworkGone = false

    private var communityId: String = ""
    private var communityName: String = ""

    companion object {
        const val TAG = "HomeFeedFragment"
        private const val BUNDLE_HOME_FRAGMENT = "bundle of home fragment"

        /**
         * creates a instance of fragment
         **/
        @JvmStatic
        fun getInstance(
            extras: HomeFeedExtras
        ): HomeFeedFragment {
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
        // todo: removed fetch data from here
    }

    override fun observeData() {
        super.observeData()

        observeLogoutResponse()
        observeErrors()

        initiateViewModel.initiateUserResponse.observe(viewLifecycleOwner) { user ->
            observeInitiateUserResponse(user)
        }

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            observeUserData(user)
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

    private fun observeLogoutResponse() {
        initiateViewModel.logoutResponse.observe(viewLifecycleOwner) {
            ViewUtils.showShortToast(requireContext(), getString(R.string.invalid_app_access))
        }
    }

    // observes error message
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
    private fun observeUserData(user: MemberViewData?) {
        if (user != null) {
            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                user.sdkClientInfo.uuid,
                binding.memberImage,
                showRoundImage = true,
                objectKey = user.updatedAt
            )
        }
    }

    //observe user data
    private fun observeInitiateUserResponse(user: MemberViewData?) {
        communityId = user?.communityId ?: ""
        communityName = user?.communityName ?: ""
        initToolbar()
        fetchData()
        viewModel.getConfig()
        viewModel.getExploreTabCount()
        viewModel.sendCommunityTabClicked(communityId, communityName)
        viewModel.sendHomeScreenOpenedEvent(LMAnalytics.Source.COMMUNITY_TAB)
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
        val list = viewModel.getHomeFeedList(requireContext())
        val items = list.filterIsInstance<HomeFeedItemViewData>()
        Log.d(
            "PUI", """
            name: ${
                items.map {
                    it.chatroom.header
                }
            }
        """.trimIndent()
        )
        homeFeedAdapter.setItemsViaDiffUtilForHome(
            list
        )
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isDBEmpty() && !userPreferences.getIsGuestUser()) {
            fetchData()
        }
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
        homeFeedAdapter = HomeFeedAdapter(userPreferences, this)
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

            //get user from local db
            viewModel.getUserFromLocalDb()

            ivSearch.setOnClickListener {
                SearchActivity.start(requireContext())
                Log.d(LOG_TAG, "search started")
            }
        }
    }

    private fun fetchData() {
        viewModel.observeChatrooms(requireContext())
    }

    override fun onChatRoomClicked(homeFeedItemViewData: HomeFeedItemViewData) {
        val chatroom = homeFeedItemViewData.chatroom
        openChatroom(chatroom)
    }

    private fun openChatroom(chatroom: ChatroomViewData) {
        val extra = ChatroomDetailExtras.Builder()
            .chatroomId(chatroom.id)
            .communityId(chatroom.communityId)
            .source(ChatroomDetailFragment.SOURCE_HOME_FEED)
            .build()
        ChatroomDetailActivity.start(requireContext(), extra)
    }

    override fun homeFeedClicked() {
        ChatroomExploreActivity.start(requireContext())
        viewModel.sendCommunityFeedClickedEvent(communityId, communityName)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val parentView = activity?.findViewById<ViewGroup>(android.R.id.content) ?: return
        if (parentView.childCount > 0) {
            parentView.getChildAt(0)?.let { view ->
                if (isConnected && wasNetworkGone) {
                    wasNetworkGone = false
                    snackBar.showMessage(view, "Internet connection restored", true)
                }
                if (!isConnected) {
                    wasNetworkGone = true
                    snackBar.showNoInternet(view)
                }
            }
        }
    }
}