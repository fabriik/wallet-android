package com.fabriik.kyc.ui.features.takephoto

import android.net.Uri
import com.fabriik.common.ui.base.FabriikEventHandler

interface TakePhotoEventHandler: FabriikEventHandler<TakePhotoContract.Event> {

    override fun handleEvent(event: TakePhotoContract.Event) {
        return when (event) {
            is TakePhotoContract.Event.BackClicked -> onBackClicked()
            is TakePhotoContract.Event.DismissClicked -> onDismissCLicked()
            is TakePhotoContract.Event.TakePhotoClicked -> onTakePhotoClicked()
            is TakePhotoContract.Event.TakePhotoFailed -> onTakePhotoFailed()
            is TakePhotoContract.Event.TakePhotoCompleted -> onTakePhotoCompleted(event.uri)
            is TakePhotoContract.Event.CameraPermissionResult -> onCameraPermissionResult(event.granted)
        }
    }

    fun onBackClicked()

    fun onDismissCLicked()

    fun onTakePhotoClicked()

    fun onTakePhotoFailed()

    fun onTakePhotoCompleted(uri: Uri)

    fun onCameraPermissionResult(granted: Boolean)
}