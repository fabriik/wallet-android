package com.fabriik.kyc.ui.features.takephoto

import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TakePhotoEventHandlerTest {

    @Mock lateinit var uriMock: Uri

    @Spy val handler = object : TakePhotoEventHandler {
        override fun onBackClicked() {}
        override fun onDismissCLicked() {}
        override fun onTakePhotoClicked() {}
        override fun onTakePhotoFailed() {}
        override fun onTakePhotoCompleted(uri: Uri) {}
        override fun onCameraPermissionResult(granted: Boolean) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(TakePhotoContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(TakePhotoContract.Event.DismissClicked)
        verify(handler).onDismissCLicked()
    }

    @Test
    fun handleEvent_takePhotoClicked_callOnTakePhotoClicked() {
        handler.handleEvent(TakePhotoContract.Event.TakePhotoClicked)
        verify(handler).onTakePhotoClicked()
    }

    @Test
    fun handleEvent_takePhotoFailed_callOnTakePhotoFailed() {
        handler.handleEvent(TakePhotoContract.Event.TakePhotoFailed)
        verify(handler).onTakePhotoFailed()
    }

    @Test
    fun handleEvent_takePhotoCompleted_callOnTakePhotoCompleted() {
        handler.handleEvent(TakePhotoContract.Event.TakePhotoCompleted(uriMock))
        verify(handler).onTakePhotoCompleted(uriMock)
    }

    @Test
    fun handleEvent_cameraPermissionResult_callOnCameraPermissionResult() {
        val granted = false
        handler.handleEvent(TakePhotoContract.Event.CameraPermissionResult(granted))
        verify(handler).onCameraPermissionResult(granted)
    }
}