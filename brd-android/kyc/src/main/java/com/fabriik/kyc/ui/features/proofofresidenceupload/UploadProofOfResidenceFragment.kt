package com.fabriik.kyc.ui.features.proofofresidenceupload

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentUploadProofOfResidenceBinding
import com.fabriik.kyc.ui.dialogs.PhotoSourcePickerBottomSheet
import com.fabriik.kyc.utils.GetPictureWithRequestCodeContract
import com.fabriik.kyc.utils.RequestPermissionWithRequestCodeContract
import com.fabriik.kyc.utils.TakePictureWithRequestCodeContract
import kotlinx.coroutines.flow.collect
import java.io.File

class UploadProofOfResidenceFragment : Fragment(),
    FabriikView<UploadProofOfResidenceContract.State, UploadProofOfResidenceContract.Effect> {

    private lateinit var binding: FragmentUploadProofOfResidenceBinding
    private val viewModel: UploadProofOfResidenceViewModel by viewModels()

    private val cameraPermissionLauncher =
        registerForActivityResult(RequestPermissionWithRequestCodeContract()) {
            if (it.second) {
                viewModel.setEvent(
                    UploadProofOfResidenceContract.Event.CameraPermissionGranted(
                        it.first
                    )
                )
            }
        }

    private val cameraLauncher = registerForActivityResult(TakePictureWithRequestCodeContract()) {
        it ?: return@registerForActivityResult

        viewModel.setEvent(
            UploadProofOfResidenceContract.Event.PhotoReady(
                requestCode = it.first,
                imageUri = it.second
            )
        )
    }

    private val galleryLauncher = registerForActivityResult(GetPictureWithRequestCodeContract()) {
        it ?: return@registerForActivityResult

        viewModel.setEvent(
            UploadProofOfResidenceContract.Event.PhotoReady(
                requestCode = it.first,
                imageUri = it.second
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload_proof_of_residence, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadProofOfResidenceBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.DismissClicked)
            }

            btnAddDocument.setOnClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.AddDocumentClicked)
            }

            btnSubmit.setOnClickListener {
                viewModel.setEvent(UploadProofOfResidenceContract.Event.SubmitClicked)
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }

        // bind fragment result listener
        lifecycleScope.launchWhenStarted {
            bindFragmentResultListener(UploadProofOfResidenceViewModel.REQUEST_KEY_DOCUMENT)
        }
    }

    override fun render(state: UploadProofOfResidenceContract.State) {
        with(binding) {
            btnSubmit.isEnabled = state.confirmEnabled
            btnAddDocument.setPreviewImage(state.documentImage)
        }
    }

    override fun handleEffect(effect: UploadProofOfResidenceContract.Effect) {
        when (effect) {
            is UploadProofOfResidenceContract.Effect.GoBack ->
                findNavController().popBackStack()

            is UploadProofOfResidenceContract.Effect.Dismiss ->
                requireActivity().finish()

            is UploadProofOfResidenceContract.Effect.GoToCompleted ->
                findNavController().navigate(
                    UploadProofOfResidenceFragmentDirections.actionToCompleted()
                )

            is UploadProofOfResidenceContract.Effect.OpenPhotoSourcePicker ->
                findNavController().navigate(
                    UploadProofOfResidenceFragmentDirections.actionOpenPhotoSourcePicker(
                        effect.requestKey
                    )
                )

            is UploadProofOfResidenceContract.Effect.OpenCamera -> {
                val context = requireContext()
                val tmpFile = File.createTempFile(effect.fileName, ".png", context.cacheDir).apply {
                    createNewFile()
                    deleteOnExit()
                }

                val tempFileUri = FileProvider.getUriForFile(context, context.packageName, tmpFile)
                cameraLauncher.launch(effect.requestKey to tempFileUri)
            }

            is UploadProofOfResidenceContract.Effect.OpenGallery ->
                galleryLauncher.launch(effect.requestKey)

            is UploadProofOfResidenceContract.Effect.RequestCameraPermission ->
                cameraPermissionLauncher.launch(
                    effect.requestKey to Manifest.permission.CAMERA
                )
        }
    }

    private fun bindFragmentResultListener(requestKey: String) {
        parentFragmentManager.setFragmentResultListener(
            requestKey, this@UploadProofOfResidenceFragment
        ) { _, bundle ->
            val resultKey = bundle.getString(PhotoSourcePickerBottomSheet.RESULT_KEY)
                ?: return@setFragmentResultListener

            viewModel.setEvent(
                UploadProofOfResidenceContract.Event.PhotoSourceSelected(
                    requestKey = requestKey,
                    resultKey = resultKey
                )
            )
        }
    }
}