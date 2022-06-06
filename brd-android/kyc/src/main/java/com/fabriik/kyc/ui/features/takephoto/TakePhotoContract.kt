package com.fabriik.kyc.ui.features.takephoto

import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import com.fabriik.common.ui.base.FabriikContract

interface TakePhotoContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked: Event()
        object DismissClicked: Event()
        object TakePhotoClicked: Event()
        object TakePhotoFailed: Event()
        class CameraStarted(val provider: ProcessCameraProvider): Event()
        class TakePhotoCompleted(val uri: Uri): Event()
        class CameraPermissionResult(val granted: Boolean): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object SetupCamera : Effect()
        object GoToNextStep : Effect()
        object RequestCameraPermission : Effect()
        class ShowSnackBar(val message: String) : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class TakePhoto(/*val type: KycUploadPhotoType*/) : Effect()
    }

    data class State(
        val title: Int,
        val description: Int,
        val kycStepProgress: Int,
        //val photoType: KycUploadPhotoType,
        val imageUri: Uri? = null,
        val nextEnabled: Boolean = false,
        val retryEnabled: Boolean = false,
        val takePhotoEnabled: Boolean = true,
        val switchCameraVisible: Boolean = false
    ): FabriikContract.State
}