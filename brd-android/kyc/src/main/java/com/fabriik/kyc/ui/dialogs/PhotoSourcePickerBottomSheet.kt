package com.fabriik.kyc.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.BottomSheetPhotoSourcePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PhotoSourcePickerBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPhotoSourcePickerBinding
    private lateinit var arguments: PhotoSourcePickerBottomSheetArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_photo_source_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = BottomSheetPhotoSourcePickerBinding.bind(view)
        arguments = PhotoSourcePickerBottomSheetArgs.fromBundle(
            getArguments() ?: bundleOf()
        )

        with(binding) {
            btnDismiss.setOnClickListener {
                dismissAllowingStateLoss()
            }

            tvCamera.setOnClickListener {
                onItemClicked(RESULT_CAMERA)
            }

            tvGallery.setOnClickListener {
                onItemClicked(RESULT_GALLERY)
            }
        }
    }

    private fun onItemClicked(result: String) {
        dismissAllowingStateLoss()

        parentFragmentManager.setFragmentResult(
            arguments.requestKey, bundleOf(RESULT_KEY to result)
        )
    }

    companion object {
        const val RESULT_KEY = "result"
        const val RESULT_CAMERA = "camera"
        const val RESULT_GALLERY = "gallery"
    }
}