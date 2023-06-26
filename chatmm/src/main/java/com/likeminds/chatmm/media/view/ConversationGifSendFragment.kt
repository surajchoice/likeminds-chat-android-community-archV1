package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.collabmates.fileutil.FileUtil
import com.collabmates.membertagging.MemberTaggingView
import com.collabmates.membertagging.model.MemberTaggingExtras
import com.collabmates.membertagging.util.MemberTaggingViewListener
import com.collabmates.sdk.media.model.GIF
import com.collabmates.sdk.sdk.SDKPreferences
import com.likeminds.chatmm.media.model.MediaExtras
import com.likeminds.chatmm.media.model.SingleUriData
import com.likeminds.chatmm.media.viewmodel.MediaViewModel
import com.likeminds.likemindschat.BrandingData
import com.likeminds.likemindschat.R
import com.likeminds.likemindschat.SDKApplication
import com.likeminds.likemindschat.base.BaseFragment
import com.likeminds.likemindschat.base.customview.edittext.LikeMindsEditTextListener
import com.likeminds.likemindschat.databinding.FragmentConversationGifSendBinding
import com.likeminds.likemindschat.utils.ProgressHelper
import com.likeminds.likemindschat.utils.databinding.ImageBindingUtil
import com.likeminds.likemindschat.utils.membertagging.MemberTaggingUtil
import javax.inject.Inject

internal class ConversationGifSendFragment :
    BaseFragment<FragmentConversationGifSendBinding, MediaViewModel>() {

    private lateinit var mediaExtras: MediaExtras
    private var singleUriData: SingleUriData? = null

    private lateinit var memberTagging: MemberTaggingView

    @Inject
    lateinit var sdkPreferences: SDKPreferences

    companion object {
        const val TAG = "ConversationGifSendFragment"
        private const val BUNDLE_CONVERSATION_GIF = "bundle of conversation gif"

        @JvmStatic
        fun getInstance(extras: MediaExtras): ConversationGifSendFragment {
            val fragment = ConversationGifSendFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_CONVERSATION_GIF, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getViewModelClass(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun getViewBinding(): FragmentConversationGifSendBinding {
        return FragmentConversationGifSendBinding.inflate(layoutInflater)
    }

    override fun drawPrimaryColor(color: Int) {
        super.drawPrimaryColor(color)
        binding.buttonSend.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun drawAdvancedColor(headerColor: Int, buttonsIconsColor: Int, textLinksColor: Int) {
        super.drawAdvancedColor(headerColor, buttonsIconsColor, textLinksColor)
        binding.buttonSend.backgroundTintList = ColorStateList.valueOf(buttonsIconsColor)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().mediaComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        mediaExtras =
            ConversationGifSendFragmentArgs.fromBundle(requireArguments()).mediaExtras
    }

    override fun setUpViews() {
        super.setUpViews()
        singleUriData = mediaExtras.mediaUris?.firstOrNull()
        initViews()
        initMemberTagging()
        initRichEditorSupport()
        initGiphy()
    }

    override fun observeData() {
        super.observeData()
        viewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    private fun initViews() {
        initGif()
        binding.buttonBack.setOnClickListener {
            cancelSend()
        }
        binding.buttonSend.setOnClickListener {
            if (sdkPreferences.getIsGuestUser()) {
                SDKApplication.getLikeMindsCallback()?.loginRequiredCallback()
                activity?.finish()
            } else {
                saveGifThumbnail()
            }
        }
    }

    private fun initMemberTagging() {
        memberTagging = binding.memberTaggingView
        memberTagging.initialize(
            MemberTaggingExtras.Builder()
                .editText(binding.etConversation)
                .darkMode(true)
                .color(
                    BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                        binding.etConversation.context,
                        R.color.pure_blue
                    )
                )
                .build()
        )

        memberTagging.addListener(object : MemberTaggingViewListener {
            override fun callApi(page: Int, searchName: String) {
                super.callApi(page, searchName)
                viewModel.fetchMembersForTagging(
                    mediaExtras.chatroomId,
                    page,
                    searchName
                )
            }
        })
    }

    private fun initGiphy() {
        mediaExtras.giphyMedia?.let {
            viewModel.getGiphyUri(requireContext(), it)
        }
        viewModel.getGiphyMedia().observe(viewLifecycleOwner) {
            it?.let { pair ->
                if (pair.first) {
                    ProgressHelper.showProgress(binding.progressBar)
                } else {
                    ProgressHelper.hideProgress(binding.progressBar)
                }
                pair.second?.let { uri ->
                    singleUriData = SingleUriData.Builder()
                        .uri(uri)
                        .fileType(GIF)
                        .build()
                    initGif()
                }
            }
        }
    }

    private fun initGif() {
        ImageBindingUtil.loadImage(binding.imageView, singleUriData?.uri())
    }

    private fun saveGifThumbnail() {
        singleUriData?.let {
            Glide.with(requireContext()).asGif().load(it.uri())
                .addListener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        initSendClick()
                        return true
                    }

                    override fun onResourceReady(
                        resource: GifDrawable,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        val thumbnailUri = FileUtil.getUriFromBitmapWithRandomName(
                            requireContext(), resource.firstFrame, isPNGFormat = true
                        )
                        singleUriData = it.toBuilder().thumbnailUri(thumbnailUri).build()
                        initSendClick()
                        return true
                    }
                }).submit()
        } ?: initSendClick()
    }

    private fun initRichEditorSupport() {
        binding.etConversation.addListener(object : LikeMindsEditTextListener {
            override fun onMediaSelected(contentUri: Uri, mimeType: String) {
            }
        })
    }

    private fun initSendClick() {
        val text = memberTagging.replaceSelectedMembers(
            binding.etConversation.editableText
        )
        val extras = mediaExtras.toBuilder()
            .conversation(text)
            .mediaUris(arrayListOf(singleUriData!!))
            .build()
        val intent = Intent()
        intent.putExtra(
            MediaActivity.BUNDLE_MEDIA_EXTRAS,
            extras
        )
        activity?.setResult(Activity.RESULT_OK, intent)
        activity?.finish()
    }

    private fun cancelSend() {
        val intent = Intent()
        activity?.setResult(Activity.RESULT_CANCELED, intent)
        activity?.finish()
    }

}