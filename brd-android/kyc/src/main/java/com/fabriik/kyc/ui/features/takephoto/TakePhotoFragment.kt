package com.fabriik.kyc.ui.features.takephoto

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentTakePhotoBinding
import kotlinx.coroutines.flow.collect
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhotoFragment : Fragment(),
    FabriikView<TakePhotoContract.State, TakePhotoContract.Effect> {

    private lateinit var binding: FragmentTakePhotoBinding
    private lateinit var cameraExecutor: ExecutorService

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val viewModel: TakePhotoViewModel by viewModels()

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.setEvent(
            TakePhotoContract.Event.CameraPermissionResult(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_take_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTakePhotoBinding.bind(view)
        cameraExecutor = Executors.newSingleThreadExecutor()

        with(binding) {

            // setup Toolbar
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(
                    TakePhotoContract.Event.BackClicked
                )
            }

            toolbar.setDismissButtonClickListener() {
                viewModel.setEvent(
                    TakePhotoContract.Event.DismissClicked
                )
            }

            // setup "Take Photo" button
            btnTakePhoto.setOnClickListener {
                viewModel.setEvent(
                    TakePhotoContract.Event.TakePhotoClicked
                )
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

        lifecycle.addObserver(viewModel)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        bindCameraUseCases()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun render(state: TakePhotoContract.State) {
        with(binding) {
            tvDocumentTitle.setText(state.title)
            tvDocumentDescription.setText(state.description)
            btnTakePhoto.isEnabled = state.takePhotoEnabled
        }
    }

    override fun handleEffect(effect: TakePhotoContract.Effect) {
        when (effect) {
            is TakePhotoContract.Effect.Back ->
                findNavController().popBackStack()

            is TakePhotoContract.Effect.Dismiss ->
                requireActivity().finish()

            is TakePhotoContract.Effect.RequestCameraPermission ->
                permissionsLauncher.launch(Manifest.permission.CAMERA)

            is TakePhotoContract.Effect.SetupCamera ->
                binding.viewPreview.post { setUpCamera() }

            is TakePhotoContract.Effect.GoToNextStep -> {
                // todo:
            } //goToNextScreen()

            is TakePhotoContract.Effect.TakePhoto ->
                takePhoto(/*effect.type*/)

            is TakePhotoContract.Effect.ShowLoading -> {
                // todo:
            }

            is TakePhotoContract.Effect.ShowSnackBar -> {
                // todo:
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                viewModel.hasBackCamera(cameraProvider) -> CameraSelector.LENS_FACING_BACK
                viewModel.hasFrontCamera(cameraProvider) -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val rotation = binding.viewPreview.display.rotation

        val preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setSurfaceProvider(binding.viewPreview.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(rotation)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun takePhoto(/*type: KycUploadPhotoType*/) {
        //val file = File.createTempFile("image_${type.id}", ".jpg")
        val file = File.createTempFile("image_test", ".jpg")
        val options = ImageCapture.OutputFileOptions
            .Builder(file)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture?.takePicture(
            options,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    viewModel.setEvent(
                        TakePhotoContract.Event.TakePhotoFailed
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModel.setEvent(
                        TakePhotoContract.Event.TakePhotoCompleted(
                            file.toUri()
                        )
                    )
                }
            }
        )
    }
}