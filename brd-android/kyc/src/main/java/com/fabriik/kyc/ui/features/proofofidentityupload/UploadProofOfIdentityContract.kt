package com.fabriik.kyc.ui.features.proofofidentityupload

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract

interface UploadProofOfIdentityContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object AddPassportClicked : Event()
        object AddIdFrontClicked : Event()
        object AddIdBackClicked : Event()

        class PhotoReady(val requestCode: String, val imageUri: Uri) : Event()
        class PhotoSourceSelected(val requestKey: String, val resultKey: String) : Event()
        class CameraPermissionGranted(val requestKey: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToAddress : Effect()

        class OpenCamera(val requestKey: String, val fileName: String) : Effect()
        class OpenGallery(val requestKey: String) : Effect()
        class OpenPhotoSourcePicker(val requestKey: String) : Effect()
        class RequestCameraPermission(val requestKey: String) : Effect()
    }

    data class State(
        val description: CharSequence,
        val idBackImage: Uri? = null,
        val idFrontImage: Uri? = null,
        val passportImage: Uri? = null,
        val confirmEnabled: Boolean = false,
        val addIdPhotosVisible: Boolean,
        val addPassportPhotoVisible: Boolean
    ) : FabriikContract.State
}