package com.likeminds.chatmm.chat.view

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.likeminds.chatmm.*
import com.likeminds.chatmm.branding.model.LMBranding
import com.likeminds.chatmm.chat.adapter.ChatPagerAdapter
import com.likeminds.chatmm.chat.model.LMChatExtras
import com.likeminds.chatmm.chat.viewmodel.ChatViewModel
import com.likeminds.chatmm.databinding.FragmentChatBinding
import com.likeminds.chatmm.dm.model.CheckDMTabViewData
import com.likeminds.chatmm.member.model.MemberViewData
import com.likeminds.chatmm.member.util.MemberImageUtil
import com.likeminds.chatmm.search.view.SearchActivity
import com.likeminds.chatmm.utils.*
import com.likeminds.chatmm.utils.connectivity.ConnectivityBroadcastReceiver
import com.likeminds.chatmm.utils.connectivity.ConnectivityReceiverListener
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.snackbar.CustomSnackBar
import javax.inject.Inject

class LMChatFragment : BaseFragment<FragmentChatBinding, ChatViewModel>(),
    ConnectivityReceiverListener {

    var dmMeta: CheckDMTabViewData? = null

    private lateinit var lmChatExtras: LMChatExtras

    private val connectivityBroadcastReceiver by lazy {
        ConnectivityBroadcastReceiver()
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    @Inject
    lateinit var snackBar: CustomSnackBar

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    private var wasNetworkGone = false

    companion object {
        const val CHAT_EXTRAS = "chat_extras"
        const val TAG = "ChatFragment"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        fun getInstance(lmChatExtras: LMChatExtras): LMChatFragment {
            val fragment = LMChatFragment()
            val bundle = Bundle()
            bundle.putParcelable(CHAT_EXTRAS, lmChatExtras)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var pagerAdapter: ChatPagerAdapter

    override fun getViewModelClass(): Class<ChatViewModel> {
        return ChatViewModel::class.java
    }

    override fun getViewBinding(): FragmentChatBinding {
        return FragmentChatBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().chatComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        checkForNotificationPermission()
        initTabLayout()
        setBranding()
        setupReceivers()
        initiateUser()
        initToolbar()
    }

    override fun receiveExtras() {
        super.receiveExtras()
        lmChatExtras = ExtrasUtil.getParcelable(
            arguments,
            CHAT_EXTRAS,
            LMChatExtras::class.java
        ) ?: throw ErrorUtil.emptyExtrasException(TAG)
    }

    override fun observeData() {
        super.observeData()

        // observes [initiateErrorMessage]
        initiateViewModel.initiateErrorMessage.observe(viewLifecycleOwner) {
            ViewUtils.showErrorMessageToast(requireContext(), it)
        }

        // observes [initiateUserResponse] live data and calls API to get configuration
        initiateViewModel.initiateUserResponse.observe(viewLifecycleOwner) {
            initPagerAdapter()
            initData()
            initiateViewModel.getConfig()
        }

        // observes [userData] ;ive data
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            observeUserData(user)
        }

        // observes [checkDMTabResponse] live data
        viewModel.checkDMTabResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                setDMMeta(response)
                updateUnreadDMCount(response.unreadDMCount)
            }
        }
    }

    //check permission for Post Notifications
    private fun checkForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                if (activity?.checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun initTabLayout() {
        binding.tabChat.apply {
            setSelectedTabIndicatorColor(LMBranding.getButtonsColor())
            setTabTextColors(Color.GRAY, LMBranding.getButtonsColor())
        }
    }

    private fun setBranding() {
        binding.apply {
            toolbarColor = LMBranding.getToolbarColor()
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

    private fun initiateUser() {
        initiateViewModel.initiateUser(
            requireContext(),
            lmChatExtras.apiKey,
            lmChatExtras.userName,
            lmChatExtras.userId,
            lmChatExtras.isGuest ?: false
        )
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
                Log.d(SDKApplication.LOG_TAG, "search started")
            }
        }
    }

    // calls api to initiate data
    private fun initData() {
        initToolbar()
        viewModel.checkDMTab()
    }

    //init tab adapter and perform operations are per selected tab
    private fun initPagerAdapter() {
        binding.viewPager.apply {
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            pagerAdapter = ChatPagerAdapter(this@LMChatFragment, lmChatExtras)
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabChat, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.apply {
                        text = getString(R.string.lm_chat_groups)
                        removeBadge()
                    }
                }

                1 -> {
                    tab.apply {
                        text = getString(R.string.lm_chat_dms)
                        val unreadDMCount = dmMeta?.unreadDMCount ?: 0
                        if (unreadDMCount > 0) {
                            val badge = orCreateBadge
                            badge.apply {
                                number = unreadDMCount
                                maxCharacterCount = 2
                                backgroundColor =
                                    ContextCompat.getColor(
                                        requireContext(),
                                        LMBranding.getButtonsColor()
                                    )
                                badgeTextColor =
                                    ContextCompat.getColor(requireContext(), R.color.lm_chat_white)
                            }
                        }

                    }
                }

                else -> {
                    throw IndexOutOfBoundsException()
                }
            }
        }.attach()
    }

    private fun setDMMeta(checkDMTabViewData: CheckDMTabViewData) {
        dmMeta = checkDMTabViewData
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

    //update the unread count on dm tab
    private fun updateUnreadDMCount(unreadDMCount: Int) {
        val dmTab = binding.tabChat.getTabAt(1)
        dmTab?.let {
            if (unreadDMCount > 0) {
                val badge = it.orCreateBadge
                badge.apply {
                    number = unreadDMCount
                    maxCharacterCount = 2
                    backgroundColor = LMBranding.getButtonsColor()
                    badgeTextColor =
                        ContextCompat.getColor(requireContext(), R.color.lm_chat_white)
                }
            }
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val parentView = activity?.findViewById<ViewGroup>(android.R.id.content) ?: return
        if (parentView.childCount > 0) {
            parentView.getChildAt(0)?.let { view ->
                if (isConnected && wasNetworkGone) {
                    wasNetworkGone = false
                    snackBar.showMessage(
                        view,
                        getString(R.string.lm_chat_internet_connection_restored),
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