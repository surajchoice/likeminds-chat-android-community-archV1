package com.likeminds.chatmm.media.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.collabmates.sdk.auth.LoginPreferences
import com.collabmates.sdk.media.model.GIF
import com.collabmates.sdk.media.model.IMAGE
import com.collabmates.sdk.media.model.PDF
import com.collabmates.sdk.media.model.VIDEO
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.likemindschat.SDKApplication
import com.likeminds.likemindschat.base.BaseFragment
import com.likeminds.likemindschat.chatroom.create.adapter.CollabcardItemAdapter
import com.likeminds.likemindschat.databinding.FragmentMediaVerticalListBinding
import com.likeminds.likemindschat.utils.ITEM_IMAGE_EXPANDED
import com.likeminds.likemindschat.utils.ITEM_PDF_EXPANDED
import com.likeminds.likemindschat.utils.ITEM_VIDEO_EXPANDED
import javax.inject.Inject

class MediaVerticalListFragment :
    BaseFragment<FragmentMediaVerticalListBinding, Nothing>() {

    private var mediaExtras: MediaExtras? = null

    @Inject
    lateinit var loginPreferences: LoginPreferences

    companion object {
        const val TAG = "MediaVerticalListFragment"
        const val BUNDLE_MEDIA_VERTICAL = "bundle of media vertical"

        @JvmStatic
        fun getInstance(extras: MediaExtras): MediaVerticalListFragment {
            val fragment = MediaVerticalListFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_MEDIA_VERTICAL, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun getViewBinding(): FragmentMediaVerticalListBinding {
        return FragmentMediaVerticalListBinding.inflate(layoutInflater)
    }

    override fun drawPrimaryColor(color: Int) {
        super.drawPrimaryColor(color)
        binding.view.setBackgroundColor(Color.WHITE)
        binding.buttonBack.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.textViewTitle.setTextColor(Color.BLACK)
        binding.textViewSubtitle.setTextColor(Color.BLACK)
        binding.textViewDate.setTextColor(Color.BLACK)
        binding.dot.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
    }

    override fun drawAdvancedColor(headerColor: Int, buttonsIconsColor: Int, textLinksColor: Int) {
        super.drawAdvancedColor(headerColor, buttonsIconsColor, textLinksColor)
        binding.view.setBackgroundColor(headerColor)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null) return
        mediaExtras = MediaVerticalListFragmentArgs.fromBundle(requireArguments()).mediaExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        initImageListView()

        binding.textViewTitle.text = mediaExtras?.title
        binding.textViewSubtitle.text = getSubTitleText()
        binding.textViewDate.text = mediaExtras?.subtitle

        binding.buttonBack.setOnClickListener { activity?.finish() }
    }

    private fun initImageListView() {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvImageList.layoutManager = linearLayoutManager
        val collabcardDocumentsItemAdapter = CollabcardItemAdapter(
            loginPreferences,
            null,
            null,
            null
        )
        binding.rvImageList.adapter = collabcardDocumentsItemAdapter
        val attachmentList = mediaExtras?.attachments?.map { attachment ->
            val viewType = when {
                attachment.type() == VIDEO -> ITEM_VIDEO_EXPANDED
                attachment.type() == PDF -> ITEM_PDF_EXPANDED
                else -> ITEM_IMAGE_EXPANDED
            }
            return@map attachment.toBuilder()
                .viewType(viewType)
                .attachments(mediaExtras?.attachments)
                .communityId(mediaExtras?.communityId)
                .build()
        }?.sortedBy { it.index() }
        if (!attachmentList.isNullOrEmpty()) {
            collabcardDocumentsItemAdapter.replace(attachmentList)
        }
    }

    private fun getSubTitleText(): CharSequence {
        val subTitleBuilder = StringBuilder()
        val imageCount = mediaExtras?.attachments?.filter { it.type() == IMAGE }?.size ?: 0
        val gifCount = mediaExtras?.attachments?.filter { it.type() == GIF }?.size ?: 0
        val videoCount = mediaExtras?.attachments?.filter { it.type() == VIDEO }?.size ?: 0
        val pdfCount = mediaExtras?.attachments?.filter { it.type() == PDF }?.size ?: 0
        if (imageCount > 0) {
            subTitleBuilder.append(imageCount)
            if (imageCount > 1) subTitleBuilder.append(" photos")
            else subTitleBuilder.append(" photo")
        }
        if (gifCount > 0) {
            subTitleBuilder.append(gifCount)
            if (gifCount > 1) subTitleBuilder.append(" gifs")
            else subTitleBuilder.append(" gif")
        }
        if (videoCount > 0) {
            if (subTitleBuilder.isNotEmpty()) subTitleBuilder.append(", ")
            subTitleBuilder.append(videoCount)
            if (videoCount > 1) subTitleBuilder.append(" videos")
            else subTitleBuilder.append(" video")
        }
        if (pdfCount > 0) {
            if (subTitleBuilder.isNotEmpty()) subTitleBuilder.append(", ")
            subTitleBuilder.append(pdfCount)
            if (pdfCount > 1) subTitleBuilder.append(" pdfs")
            else subTitleBuilder.append(" pdf")
        }
        return subTitleBuilder.toString()
    }
}