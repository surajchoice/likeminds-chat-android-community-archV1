package com.likeminds.chatmm.homefeed.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.chatmm.InitiateViewModel
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.chatroom.model.ChatroomDetailExtras
import com.likeminds.chatmm.chatroom.model.ChatroomViewData
import com.likeminds.chatmm.chatroom.view.ChatroomDetailActivity
import com.likeminds.chatmm.databinding.FragmentHomeFeedBinding
import com.likeminds.chatmm.homefeed.model.ChatViewData
import com.likeminds.chatmm.homefeed.model.GroupChatResponse
import com.likeminds.chatmm.homefeed.model.HomeFeedExtras
import com.likeminds.chatmm.homefeed.view.adapter.HomeFeedAdapter
import com.likeminds.chatmm.homefeed.viewmodel.HomeFeedViewModel
import com.likeminds.chatmm.search.view.SearchActivity
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.UserPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.ViewUtils.hide
import com.likeminds.chatmm.utils.ViewUtils.show
import com.likeminds.chatmm.utils.customview.BaseFragment
import com.likeminds.chatmm.utils.observeInLifecycle
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFeedFragment : BaseFragment<FragmentHomeFeedBinding, HomeFeedViewModel>(),
    HomeFeedAdapter.HomeFeedAdapterListener {

    private lateinit var extras: HomeFeedExtras
    private lateinit var homeFeedAdapter: HomeFeedAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var sdkPreferences: SDKPreferences

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
        initiateUser()
        initRecyclerView()
        initToolbar()
        fetchData()
    }

    override fun observeData() {
        super.observeData()

        // observes error message
        observeErrors()

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
    }

    private fun updateChatrooms() {
        homeFeedAdapter.setItemsViaDiffUtilForHome(
            viewModel.getHomeFeedList(requireContext())
        )
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
        homeFeedAdapter = HomeFeedAdapter(userPreferences, sdkPreferences, this)
        binding.rvHomeFeed.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = homeFeedAdapter
            show()
        }
        binding.layoutRemoveAccess.root.hide()
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        //if user is guest user hide, profile icon from toolbar
        binding.memberImage.isVisible = !isGuestUser

        // todo:
        //get user from local db
//        viewModel.getUserFromLocalDb()

        binding.ivSearch.setOnClickListener {
            SearchActivity.start(requireContext())
            Log.d(SDKApplication.LOG_TAG, "search started")
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
        TODO("Not yet implemented")
    }
}