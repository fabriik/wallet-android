package com.fabriik.kyc.ui.features.takephoto

import android.net.Uri
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
import com.fabriik.kyc.data.model.DocumentData
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
        object RequestCameraPermission : Effect()
        class ShowToast(val message: String) : Effect()
        class SetupCamera(val preferredLensFacing: Int) : Effect()

        class TakePhoto(
            val fileName: String
        ) : Effect()

        class GoToPreview(
            val currentData: DocumentData,
            val documentData: Array<DocumentData>,
            val documentType: DocumentType
        ) : Effect()
    }

    data class State(
        val title: Int,
        val description: Int,
        val imageUri: Uri? = null,
        val documentType: DocumentType,
        val documentSide: DocumentSide,
        val takePhotoEnabled: Boolean = true,
        val finderViewType: PhotoFinderView.Type,
        val preferredLensFacing: Int,
        val backEnabled: Boolean = true
    ) : FabriikContract.State
}