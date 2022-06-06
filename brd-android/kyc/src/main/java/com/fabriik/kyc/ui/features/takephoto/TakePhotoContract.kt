package com.fabriik.kyc.ui.features.takephoto

import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.ui.customview.PhotoFinderView

interface TakePhotoContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object TakePhotoClicked : Event()
        object TakePhotoFailed : Event()
        class TakePhotoCompleted(val uri: Uri) : Event()
        class CameraPermissionResult(val granted: Boolean) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object SetupCamera : Effect()
        object RequestCameraPermission : Effect()
        class ShowSnackBar(val message: String) : Effect()
        class ShowLoading(val show: Boolean) : Effect()

        class TakePhoto(
            val fileName: String
        ) : Effect()

        class GoToPreview(
            val documentSide: DocumentSide,
            val documentType: DocumentType,
            val imageUri: Uri
        ) : Effect()
    }

    data class State(
        val title: Int,
        val description: Int,
        val imageUri: Uri? = null,
        val documentType: DocumentType,
        val documentSide: DocumentSide,
        val takePhotoEnabled: Boolean = true,
        val finderViewType: PhotoFinderView.Type
    ) : FabriikContract.State
}