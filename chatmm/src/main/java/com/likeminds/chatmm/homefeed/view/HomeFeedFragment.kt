package com.likeminds.chatmm.homefeed.view

import android.os.Bundle
import com.likeminds.chatmm.InitiateViewModel
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.FragmentHomeFeedBinding
import com.likeminds.chatmm.homefeed.model.GroupChatResponse
import com.likeminds.chatmm.homefeed.model.HomeFeedExtras
import com.likeminds.chatmm.homefeed.viewmodel.HomeFeedViewModel
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseFragment
import javax.inject.Inject

class HomeFeedFragment : BaseFragment<FragmentHomeFeedBinding, HomeFeedViewModel>() {

    private lateinit var extras: HomeFeedExtras

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
    }

    override fun observeData() {
        super.observeData()

        // observes error message
        observeErrors()
    }

    private fun observeErrors() {
        initiateViewModel.initiateErrorMessage.observe(viewLifecycleOwner) {
            ViewUtils.showErrorMessageToast(requireContext(), it)
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
}