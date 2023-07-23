package com.likeminds.chatmm.media.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.likeminds.chatmm.databinding.FragmentImageCropBinding
import com.likeminds.chatmm.media.model.ImageCropExtras
import com.likeminds.chatmm.media.model.SingleUriData
import com.likeminds.chatmm.utils.ValueUtils.getMediaType
import com.likeminds.chatmm.utils.file.util.FileUtil
import kotlinx.coroutines.launch

class ImageCropFragment : Fragment() {

    companion object {
        const val REQUEST_KEY = "image_edit"
        const val BUNDLE_ARG_URI = "arg_uri"

        const val TAG = "ImageCropFragment"
        private const val BUNDLE_IMAGE_CROP = "bundle of image crop"

        @JvmStatic
        fun getInstance(extras: ImageCropExtras): ImageCropFragment {
            val fragment = ImageCropFragment()
            val bundle = Bundle()
            bundle.putParcelable(BUNDLE_IMAGE_CROP, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var singleUriData: SingleUriData? = null
    private var cropSquare = false
    private var isFromActivity = false

    lateinit var extras: ImageCropExtras

    private lateinit var binding: FragmentImageCropBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null) return
        singleUriData = ImageCropFragmentArgs.fromBundle(requireArguments()).singleUriData
        isFromActivity = ImageCropFragmentArgs.fromBundle(requireArguments()).isFromActivity
        cropSquare = ImageCropFragmentArgs.fromBundle(requireArguments()).cropSquare
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentImageCropBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cropImageView.setImageUriAsync(singleUriData?.uri)
        if (cropSquare) {
            binding.cropImageView.setFixedAspectRatio(true)
            binding.cropImageView.setAspectRatio(1, 1)
        }
        binding.cropImageView.setOnCropImageCompleteListener { imageView, result ->
            lifecycleScope.launch {
                imageView.context.let {
                    if (result.isSuccessful) {
                        val uri = result.uriContent
                        if (isFromActivity) {
                            val i = Intent()
                            i.data = uri
                            activity?.setResult(Activity.RESULT_OK, i)
                            activity?.finish()
                        } else {
                            setFragmentResult(
                                REQUEST_KEY,
                                bundleOf(
                                    BUNDLE_ARG_URI to SingleUriData.Builder().uri(uri ?: Uri.EMPTY)
                                        .fileType(uri.getMediaType(requireContext()) ?: "").build()
                                )
                            )
                            findNavController().navigateUp()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Image crop failed: " + result.error?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        cancelCrop()
                    }
                }
            }
        }

        binding.btnDone.setOnClickListener {
            cropImage()
        }

        binding.btnCancel.setOnClickListener {
            cancelCrop()
        }

        binding.btnRotate.setOnClickListener {
            binding.cropImageView.rotateImage(-90)
        }

    }

    private fun cropImage() {
        val cropImageUri = Uri.fromFile(FileUtil.createImageFile(requireContext()))
        binding.cropImageView.saveCroppedImageAsync(
            cropImageUri,
            Bitmap.CompressFormat.JPEG,
            95,
            0,
            0
        )
    }

    private fun cancelCrop() {
        if (isFromActivity) {
            val i = Intent()
            activity?.setResult(Activity.RESULT_CANCELED, i)
            activity?.finish()
            return
        }
        findNavController().navigateUp()
    }
}