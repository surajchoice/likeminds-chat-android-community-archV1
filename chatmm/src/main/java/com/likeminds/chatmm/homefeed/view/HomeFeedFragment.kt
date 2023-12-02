package com.likeminds.chatmm.homefeed.view

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkInfo
import com.likeminds.chatmm.*
import com.likeminds.chatmm.SDKApplication.Companion.LOG_TAG
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chat.model.LMChatExtras
import com.likeminds.chatmm.chat.model.SDKInitiateSource
import com.likeminds.chatmm.chatroom.detail.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.detail.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailActivity
import com.likeminds.chatmm.chatroom.detail.view.ChatroomDetailFragment
import com.likeminds.chatmm.chatroom.explore.view.ChatroomExploreActivity
import com.likeminds.chatmm.databinding.FragmentHomeFeedBinding
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
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.connectivity.ConnectivityBroadcastReceiver
import com.likeminds.chatmm.utils.connectivity.ConnectivityReceiverListener
import com.likeminds.chatmm.utils.customview.BaseAppCompatActivity
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.permissions.*
import com.likeminds.chatmm.utils.snackbar.CustomSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFeedFragment : BaseFragment<FragmentHomeFeedBinding, HomeFeedViewModel>(),
    HomeFeedAdapterListener,
    ConnectivityReceiverListener {

    private lateinit var extras: LMChatExtras
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

    private val connectivityBroadcastReceiver by lazy {
        ConnectivityBroadcastReceiver()
    }

    companion object {
        const val TAG = "HomeFeedFragment"
        private const val BUNDLE_HOME_FRAGMENT = "bundle of home fragment"

        /**
         * creates a instance of fragment
         **/
        @JvmStatic
        fun getInstance(
            extras: LMChatExtras
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
        extras = ExtrasUtil.getParcelable(
            requireArguments(),
            BUNDLE_HOME_FRAGMENT,
            LMChatExtras::class.java
        ) ?: throw emptyExtrasException(TAG)

        isGuestUser = extras.isGuest ?: false
    }

    override fun setUpViews() {
        super.setUpViews()
        checkForNotificationPermission()
        setBranding()
        if (extras.sdkInitiateSource == SDKInitiateSource.HOME_FEED) {
            binding.toolbar.show()
            setupReceivers()
            initiateUser()
        } else {
            binding.toolbar.hide()
            initData()
        }
        initRecyclerView()
        initToolbar()
        fetchData()
    }

    //check permission for Post Notifications
    private fun checkForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val extras = LMChatPermission.getNotificationPermissionData(requireContext())
            LMChatPermissionManager.performTaskWithPermissionExtras(
                requireActivity() as BaseAppCompatActivity,
                {
                    Log.d(LOG_TAG, "notification permission approved")
                },
                extras,
                showInitialPopup = true,
                showDeniedPopup = true,
                lmChatPermissionDeniedCallback = object : LMChatPermissionDeniedCallback {
                    override fun onDeny() {
                        Log.d(LOG_TAG, "notification permission denied")
                    }

                    override fun onCancel() {
                        Log.d(LOG_TAG, "notification permission cancelled")
                    }
                }
            )
        } else {
            return
        }
    }

    //register receivers to the activity
    private fun setupReceivers() {
        connectivityBroadcastReceiver.setListener(this)
        activity?.registerReceiver(
            connectivityBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun observeData() {
        super.observeData()

        observeLogoutResponse()
        observeErrors()

        initiateViewModel.initiateUserResponse.observe(viewLifecycleOwner) { user ->
            if (extras.sdkInitiateSource == SDKInitiateSource.HOME_FEED) {
                observeInitiateUserResponse(user)
            }
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
            communityId = user.communityId ?: ""
            communityName = user.communityName ?: ""

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
        initData()
        initiateViewModel.getConfig()
    }

    // initializes home feed data
    private fun initData() {
        initToolbar()
        fetchData()
        startSync()

        viewModel.observeLiveHomeFeed(requireContext())
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
        homeFeedAdapter.setItemsViaDiffUtilForHome(
            viewModel.getHomeFeedList(requireContext())
        )
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isDBEmpty() && !userPreferences.getIsGuestUser()) {
            startSync()
        }
    }

    private fun startSync() {
        val pairOfObservers = viewModel.syncChatrooms(requireContext())

        val firstTimeObserver = pairOfObservers?.first
        val appConfigObserver = pairOfObservers?.second
        lifecycleScope.launch(Dispatchers.Main) {
            when {
                firstTimeObserver != null -> {
                    val syncStartedAt = System.currentTimeMillis()
                    firstTimeObserver.observe(this@HomeFeedFragment, Observer { workInfoList ->
                        workInfoList.forEach { workInfo ->
                            if (workInfo.state != WorkInfo.State.SUCCEEDED) {
                                return@Observer
                            }
                        }
                        val timeTaken = (System.currentTimeMillis() - syncStartedAt) / 1000f
                        viewModel.setWasChatroomFetched(true)
                        viewModel.refetchChatrooms()
                        viewModel.sendSyncCompleteEvent(timeTaken)
                    })
                }

                appConfigObserver != null -> {
                    appConfigObserver.observe(this@HomeFeedFragment, Observer { workInfoList ->
                        workInfoList.forEach { workInfo ->
                            if (workInfo.state != WorkInfo.State.SUCCEEDED) {
                                return@Observer
                            }
                        }
                        viewModel.refetchChatrooms()
                    })
                }
            }
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
        viewModel.observeChatrooms()
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
                    snackBar.showMessage(
                        view,
                        getString(R.string.internet_connection_restored),
                        true
                    )
                }
                if (!isConnected) {
                    wasNetworkGone = true
                    snackBar.showNoInternet(view)
                }
            }
        }
    }
}