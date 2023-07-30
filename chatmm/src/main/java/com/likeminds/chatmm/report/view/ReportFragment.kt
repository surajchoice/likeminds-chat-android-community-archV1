package com.likeminds.chatmm.report.view

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import com.google.android.flexbox.*
import com.likeminds.chatmm.R
import com.likeminds.chatmm.SDKApplication
import com.likeminds.chatmm.databinding.FragmentReportBinding
import com.likeminds.chatmm.report.model.*
import com.likeminds.chatmm.report.view.adapter.ReportAdapter
import com.likeminds.chatmm.report.view.adapter.ReportAdapterListener
import com.likeminds.chatmm.report.viewmodel.ReportViewModel
import com.likeminds.chatmm.utils.ErrorUtil.emptyExtrasException
import com.likeminds.chatmm.utils.SDKPreferences
import com.likeminds.chatmm.utils.ViewUtils
import com.likeminds.chatmm.utils.customview.BaseFragment
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ReportFragment : BaseFragment<FragmentReportBinding, ReportViewModel>(),
    ReportAdapterListener {

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    override fun getViewModelClass(): Class<ReportViewModel> {
        return ReportViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().reportComponent()?.inject(this)
    }

    override fun getViewBinding(): FragmentReportBinding {
        return FragmentReportBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "ReportFragment"
        const val REPORT_RESULT = "REPORT_RESULT"
    }

    private lateinit var extras: ReportExtras
    private lateinit var mAdapter: ReportAdapter
    private var tagSelected: ReportTagViewData? = null

    override fun receiveExtras() {
        super.receiveExtras()
        extras = requireActivity().intent?.getBundleExtra("bundle")
            ?.getParcelable(ReportActivity.REPORT_EXTRAS)
            ?: throw emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
        initViewAsType()
        initListeners()
        getReportTags()
    }

    override fun reportTagSelected(reportTagViewData: ReportTagViewData) {
        super.reportTagSelected(reportTagViewData)
        //check if [Others] is selected, edit text for reason should be visible
        binding.etOthers.isVisible = reportTagViewData.name.contains("Others", true)

        //replace list in adapter and only highlight selected tag
        mAdapter.replace(
            mAdapter.items()
                .map {
                    (it as ReportTagViewData).toBuilder()
                        .isSelected(it.id == reportTagViewData.id)
                        .build()
                })
    }

    //setup recycler view
    private fun initRecyclerView() {
        mAdapter = ReportAdapter(this)
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext())
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvReport.layoutManager = flexboxLayoutManager
        binding.rvReport.adapter = mAdapter
    }

    //set headers and sub header as per report type
    private fun initViewAsType() {
        binding.apply {
            Log.d(SDKApplication.LOG_TAG, "Push Reports opened")
            //set headers and sub header as per report type
            when (extras.type) {
                REPORT_TYPE_MEMBER -> {
                    tvReportTitle.text = getString(R.string.report_s, "Member")
                    tvReportSubHeader.text = getString(R.string.report_sub_header, "member")
                    viewModel.sendMemberProfileReport(
                        extras.uuid,
                        extras.communityId
                    )
                }
                REPORT_TYPE_CONVERSATION -> {
                    tvReportTitle.text = getString(R.string.report_s, "Message")
                    tvReportSubHeader.text = getString(R.string.report_sub_header, "message")
                }
            }
        }
    }

    // initializes click listeners
    private fun initListeners() {
        binding.apply {
            ivCross.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnPostReport.setOnClickListener {
                //get selected tag
                tagSelected = mAdapter.items()
                    .map { it as ReportTagViewData }
                    .find { it.isSelected }

                //get reason for [edittext]
                val reason = etOthers.text?.trim().toString()
                val isOthersSelected = tagSelected?.name?.contains("Others", true)

                //if no tag is selected
                if (tagSelected == null) {
                    ViewUtils.showShortSnack(
                        root,
                        getString(R.string.please_select_at_least_one_report_tag)
                    )
                    return@setOnClickListener
                }

                //if [Others] is selected but reason is empty
                if (isOthersSelected == true && reason.isEmpty()) {
                    ViewUtils.showShortSnack(
                        root,
                        getString(R.string.please_enter_a_reason)
                    )
                    return@setOnClickListener
                }

                //call post api
                viewModel.postReport(
                    tagSelected?.id,
                    extras.uuid,
                    extras.conversationId,
                    reason
                )
            }
        }
    }

    //get tags
    private fun getReportTags() {
        viewModel.getReportTags(extras.type)
    }

    // observes live data
    override fun observeData() {
        super.observeData()

        observeErrors()

        // observes [listOfTagViewData] live data
        viewModel.listOfTagViewData.observe(viewLifecycleOwner) { list ->
            mAdapter.replace(list)
        }

        // observes [postReportResponse] live data
        viewModel.postReportResponse.observe(viewLifecycleOwner) { response ->
            if (response) {
                Log.d(SDKApplication.LOG_TAG, "reports send successfully")

                //send analytics events
                sendReportSuccessEvent()

                //set result, from where the result is coming.
                val intent = Intent()
                requireActivity().setResult(Activity.RESULT_OK, intent)
            } else {
                ViewUtils.showSomethingWentWrongToast(requireContext())
                requireActivity().setResult(Activity.RESULT_CANCELED)
            }
            requireActivity().finish()
        }
    }

    // observes errors
    private fun observeErrors() {
        viewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is ReportViewModel.ErrorMessageEvent.GetReportTags -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                    requireActivity().setResult(Activity.RESULT_CANCELED)
                    requireActivity().finish()
                }
                is ReportViewModel.ErrorMessageEvent.PostReport -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                    requireActivity().setResult(Activity.RESULT_CANCELED)
                    requireActivity().finish()
                }
            }
        }
    }

    //send report success event depending upon which type of the report is created
    private fun sendReportSuccessEvent() {
        when (extras.type) {
            REPORT_TYPE_MEMBER -> {
                viewModel.sendMemberProfileReportConfirmed(
                    extras.communityId,
                    extras.uuid,
                    tagSelected?.name
                )
            }
            REPORT_TYPE_CONVERSATION -> {
                viewModel.sendMessageReportedEvent(
                    extras.conversationId,
                    tagSelected?.name,
                    extras.chatroomId,
                    extras.communityId,
                    extras.chatroomName,
                    extras.conversationType
                )
            }
        }
    }
}