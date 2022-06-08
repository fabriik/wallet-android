package com.fabriik.kyc.ui.features.takephoto

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.kyc.R
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.data.model.DocumentData
import com.fabriik.kyc.ui.customview.PhotoFinderView

class TakePhotoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<TakePhotoContract.State, TakePhotoContract.Event, TakePhotoContract.Effect>(
    application, savedStateHandle
), LifecycleObserver {

    private lateinit var arguments: TakePhotoFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = TakePhotoFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState(): TakePhotoContract.State {
        val documentSide = if (arguments.documentData.isEmpty()) {
            DocumentSide.FRONT
        } else {
            DocumentSide.BACK
        }

        return TakePhotoContract.State(
            title = getTitle(
                documentType = arguments.documentType,
                documentSide = documentSide
            ),
            description = getDescription(
                documentType = arguments.documentType,
                documentSide = documentSide
            ),
            documentSide = documentSide,
            documentType = arguments.documentType,
            preferredLensFacing = when (arguments.documentType) {
                DocumentType.SELFIE -> CameraSelector.LENS_FACING_FRONT
                else -> CameraSelector.LENS_FACING_BACK
            },
            finderViewType = when (arguments.documentType) {
                DocumentType.SELFIE -> PhotoFinderView.Type.SELFIE
                else -> PhotoFinderView.Type.DOCUMENT
            }
        )
    }

    override fun handleEvent(event: TakePhotoContract.Event) {
        when (event) {
            is TakePhotoContract.Event.BackClicked ->
                setEffect { TakePhotoContract.Effect.Back }

            is TakePhotoContract.Event.DismissClicked ->
                setEffect { TakePhotoContract.Effect.Dismiss }

            is TakePhotoContract.Event.CameraPermissionResult ->
                setEffect {
                    if (event.granted) {
                        TakePhotoContract.Effect.SetupCamera(
                            currentState.preferredLensFacing
                        )
                    } else {
                        TakePhotoContract.Effect.RequestCameraPermission
                    }
                }

            is TakePhotoContract.Event.TakePhotoFailed ->
                setEffect {
                    TakePhotoContract.Effect.ShowSnackBar(
                        getString(R.string.TakePhoto_DefaultErrorMessage)
                    )
                }

            is TakePhotoContract.Event.TakePhotoCompleted ->
                setEffect {
                    TakePhotoContract.Effect.GoToPreview(
                        currentData = DocumentData(
                            documentSide = currentState.documentSide,
                            imageUri = event.uri
                        ),
                        documentData = arguments.documentData,
                        documentType = currentState.documentType
                    )
                }

            is TakePhotoContract.Event.TakePhotoClicked ->
                setEffect {
                    TakePhotoContract.Effect.TakePhoto(
                        generateFileName(
                            type = currentState.documentType,
                            side = currentState.documentSide
                        )
                    )
                }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onLifecycleStart() {
        val cameraPerm = ContextCompat.checkSelfPermission(
            getApplication<Application>().applicationContext, Manifest.permission.CAMERA
        )

        setEffect {
            if (cameraPerm == PackageManager.PERMISSION_GRANTED) {
                TakePhotoContract.Effect.SetupCamera(
                    currentState.preferredLensFacing
                )
            } else {
                TakePhotoContract.Effect.RequestCameraPermission
            }
        }
    }

    internal fun hasBackCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    internal fun hasFrontCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private fun generateFileName(type: DocumentType, side: DocumentSide) =
        "image_${type.id}_${side.id}"

    private fun getTitle(documentType: DocumentType, documentSide: DocumentSide) =
        when (documentType) {
            DocumentType.SELFIE -> R.string.TakePhoto_Selfie_Title
            DocumentType.PASSPORT -> R.string.TakePhoto_Passport_Title
            else -> when (documentSide) {
                DocumentSide.BACK -> R.string.TakePhoto_IdBack_Title
                DocumentSide.FRONT -> R.string.TakePhoto_IdFront_Title
            }
        }

    private fun getDescription(documentType: DocumentType, documentSide: DocumentSide) =
        when (documentType) {
            DocumentType.SELFIE -> R.string.TakePhoto_Selfie_Description
            DocumentType.PASSPORT -> R.string.TakePhoto_Passport_Description
            else -> when (documentSide) {
                DocumentSide.BACK -> R.string.TakePhoto_IdBack_Description
                DocumentSide.FRONT -> R.string.TakePhoto_IdFront_Description
            }
        }
}