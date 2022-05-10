package com.fabriik.kyc.ui.features.proofofidentityupload

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentUploadProofOfIdentityBinding
import com.fabriik.kyc.ui.dialogs.PhotoSourcePickerBottomSheet
import com.fabriik.kyc.utils.GetPictureWithRequestCodeContract
import com.fabriik.kyc.utils.RequestPermissionWithRequestCodeContract
import com.fabriik.kyc.utils.TakePictureWithRequestCodeContract
import kotlinx.coroutines.flow.collect
import java.io.File

class UploadProofOfIdentityFragment : Fragment(),
    FabriikView<UploadProofOfIdentityContract.State, UploadProofOfIdentityContract.Effect> {

    private lateinit var binding: FragmentUploadProofOfIdentityBinding
    private val viewModel: UploadProofOfIdentityViewModel by viewModels()

    private val cameraPermissionLauncher =
        registerForActivityResult(RequestPermissionWithRequestCodeContract()) {
            if (it.second) {
                viewModel.setEvent(
                    UploadProofOfIdentityContract.Event.CameraPermissionGranted(
                        it.first
                    )
                )
            }
        }

    private val cameraLauncher = registerForActivityResult(TakePictureWithRequestCodeContract()) {
        it ?: return@registerForActivityResult

        viewModel.setEvent(
            UploadProofOfIdentityContract.Event.PhotoReady(
                requestCode = it.first,
                imageUri = it.second
            )
        )
    }

    private val galleryLauncher = registerForActivityResult(GetPictureWithRequestCodeContract()) {
        it ?: return@registerForActivityResult

        viewModel.setEvent(
            UploadProofOfIdentityContract.Event.PhotoReady(
                requestCode = it.first,
                imageUri = it.second
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_upload_proof_of_identity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadProofOfIdentityBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.DismissClicked)
            }

            btnAddIdBack.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddIdBackClicked)
            }

            btnAddIdFront.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddIdFrontClicked)
            }

            btnAddPassport.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.AddPassportClicked)
            }

            btnConfirm.setOnClickListener {
                viewModel.setEvent(UploadProofOfIdentityContract.Event.ConfirmClicked)
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

        // bind fragment result listeners
        lifecycleScope.launchWhenStarted {
            bindFragmentResultListener(UploadProofOfIdentityViewModel.REQUEST_KEY_PASSPORT)
            bindFragmentResultListener(UploadProofOfIdentityViewModel.REQUEST_KEY_BACK_SIDE)
            bindFragmentResultListener(UploadProofOfIdentityViewModel.REQUEST_KEY_FRONT_SIDE)
        }
    }

    override fun render(state: UploadProofOfIdentityContract.State) {
        with(binding) {
            tvDescription.text = state.description
            btnConfirm.isEnabled = state.confirmEnabled
            btnAddPassport.isVisible = state.addPassportPhotoVisible
            layoutIdButtons.isVisible = state.addIdPhotosVisible

            btnAddIdBack.setPreviewImage(state.idBackImage)
            btnAddIdFront.setPreviewImage(state.idFrontImage)
            btnAddPassport.setPreviewImage(state.passportImage)
        }
    }

    override fun handleEffect(effect: UploadProofOfIdentityContract.Effect) {
        when (effect) {
            is UploadProofOfIdentityContract.Effect.GoBack ->
                findNavController().popBackStack()

            is UploadProofOfIdentityContract.Effect.Dismiss ->
                requireActivity().finish()

            is UploadProofOfIdentityContract.Effect.GoToAddress ->
                findNavController().navigate(
                    UploadProofOfIdentityFragmentDirections.actionToAddress()
                )

            is UploadProofOfIdentityContract.Effect.OpenPhotoSourcePicker ->
                findNavController().navigate(
                    UploadProofOfIdentityFragmentDirections.actionOpenPhotoSourcePicker(
                        effect.requestKey
                    )
                )

            is UploadProofOfIdentityContract.Effect.OpenCamera -> {
                val context = requireContext()
                val tmpFile = File.createTempFile(effect.fileName, ".png", context.cacheDir).apply {
                    createNewFile()
                    deleteOnExit()
                }

                val tempFileUri = FileProvider.getUriForFile(context, context.packageName, tmpFile)
                cameraLauncher.launch(effect.requestKey to tempFileUri)
            }

            is UploadProofOfIdentityContract.Effect.OpenGallery ->
                galleryLauncher.launch(effect.requestKey)

            is UploadProofOfIdentityContract.Effect.RequestCameraPermission ->
                cameraPermissionLauncher.launch(
                    effect.requestKey to Manifest.permission.CAMERA
                )
        }
    }

    private fun bindFragmentResultListener(requestKey: String) {
        parentFragmentManager.setFragmentResultListener(
            requestKey, this@UploadProofOfIdentityFragment
        ) { _, bundle ->
            val resultKey = bundle.getString(PhotoSourcePickerBottomSheet.RESULT_KEY)
                ?: return@setFragmentResultListener

            viewModel.setEvent(
                UploadProofOfIdentityContract.Event.PhotoSourceSelected(
                    requestKey = requestKey,
                    resultKey = resultKey
                )
            )
        }
    }
}