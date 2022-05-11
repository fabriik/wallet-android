package com.fabriik.kyc.ui.features.proofofresidenceupload

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract

interface UploadProofOfResidenceContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object SubmitClicked : Event()
        object AddDocumentClicked : Event()

        class PhotoReady(val requestCode: String, val imageUri: Uri) : Event()
        class PhotoSourceSelected(val requestKey: String, val resultKey: String) : Event()
        class CameraPermissionGranted(val requestKey: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToCompleted : Effect()

        class OpenCamera(val requestKey: String, val fileName: String) : Effect()
        class OpenGallery(val requestKey: String) : Effect()
        class OpenPhotoSourcePicker(val requestKey: String) : Effect()
        class RequestCameraPermission(val requestKey: String) : Effect()
    }

    data class State(
        val documentImage: Uri? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}