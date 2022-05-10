package com.fabriik.kyc.ui.features.proofofresidenceupload

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.ui.dialogs.PhotoSourcePickerBottomSheet

class UploadProofOfResidenceViewModel(
    application: Application
) : FabriikViewModel<UploadProofOfResidenceContract.State, UploadProofOfResidenceContract.Event, UploadProofOfResidenceContract.Effect>(
    application
) {

    override fun createInitialState() = UploadProofOfResidenceContract.State()

    override fun handleEvent(event: UploadProofOfResidenceContract.Event) {
        when (event) {
            is UploadProofOfResidenceContract.Event.BackClicked ->
                setEffect { UploadProofOfResidenceContract.Effect.GoBack }

            is UploadProofOfResidenceContract.Event.DismissClicked ->
                setEffect { UploadProofOfResidenceContract.Effect.Dismiss }

            is UploadProofOfResidenceContract.Event.SubmitClicked ->
                setEffect { UploadProofOfResidenceContract.Effect.GoToCompleted }
            
            is UploadProofOfResidenceContract.Event.AddDocumentClicked ->
                setEffect {
                    UploadProofOfResidenceContract.Effect.OpenPhotoSourcePicker(
                        REQUEST_KEY_DOCUMENT
                    )
                }

            is UploadProofOfResidenceContract.Event.CameraPermissionGranted ->
                setEffect {
                    UploadProofOfResidenceContract.Effect.OpenCamera(
                        requestKey = event.requestKey,
                        fileName = event.requestKey
                    )
                }

            is UploadProofOfResidenceContract.Event.PhotoSourceSelected -> {
                when (event.resultKey) {
                    PhotoSourcePickerBottomSheet.RESULT_CAMERA ->
                        setEffect {
                            UploadProofOfResidenceContract.Effect.RequestCameraPermission(
                                event.requestKey
                            )
                        }

                    PhotoSourcePickerBottomSheet.RESULT_GALLERY ->
                        setEffect { UploadProofOfResidenceContract.Effect.OpenGallery(event.requestKey) }
                }
            }

            is UploadProofOfResidenceContract.Event.PhotoReady ->
                setState {
                    when (event.requestCode) {
                        REQUEST_KEY_DOCUMENT -> copy(documentImage = event.imageUri).validate()
                        else -> this
                    }
                }
        }
    }

    private fun UploadProofOfResidenceContract.State.validate() = copy(
        confirmEnabled = documentImage != null
    )

    companion object {
        const val REQUEST_KEY_DOCUMENT = "document"
    }
}