package com.fabriik.signup.ui.kyc.idupload

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import com.fabriik.signup.R
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.signup.utils.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class KycBaseIdUploadViewModel(
    application: Application
) : FabriikViewModel<KycBaseIdUploadContract.State, KycBaseIdUploadContract.Event, KycBaseIdUploadContract.Effect>(
    application
), LifecycleObserver {

    override fun handleEvent(event: KycBaseIdUploadContract.Event) {
        when (event) {
            is KycBaseIdUploadContract.Event.CameraPermissionResult ->
                setEffect {
                    if (event.granted) {
                        KycBaseIdUploadContract.Effect.SetupCamera
                    } else {
                        KycBaseIdUploadContract.Effect.RequestCameraPermission
                    }
                }

            is KycBaseIdUploadContract.Event.CameraStarted ->
                setState {
                    copy(
                        switchCameraVisible = try {
                            hasBackCamera(event.provider) && hasFrontCamera(event.provider)
                        } catch (ex: CameraInfoUnavailableException) {
                            false
                        }
                    )
                }

            is KycBaseIdUploadContract.Event.TakePhotoFailed ->
                setEffect {
                    KycBaseIdUploadContract.Effect.ShowSnackBar(
                        getString(R.string.KycIdUpload_DefaultErrorMessage)
                    )
                }

            is KycBaseIdUploadContract.Event.TakePhotoCompleted ->
                setState {
                    copy(
                        imageUri = event.uri,
                        nextEnabled = true,
                        retryEnabled = true,
                        takePhotoEnabled = false
                    )
                }

            is KycBaseIdUploadContract.Event.SwitchCameraClicked ->
                setEffect {
                    KycBaseIdUploadContract.Effect.SwitchCamera
                }

            is KycBaseIdUploadContract.Event.NextClicked ->
                onNextClicked()

            is KycBaseIdUploadContract.Event.RetryClicked ->
                setState {
                    copy(
                        imageUri = null,
                        nextEnabled = false,
                        retryEnabled = false,
                        takePhotoEnabled = true,
                    )
                }

            is KycBaseIdUploadContract.Event.TakePhotoClicked ->
                setEffect {
                    KycBaseIdUploadContract.Effect.TakePhoto(currentState.photoType)
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
                KycBaseIdUploadContract.Effect.SetupCamera
            } else {
                KycBaseIdUploadContract.Effect.RequestCameraPermission
            }
        }
    }

    private fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            setEffect {
                KycBaseIdUploadContract.Effect.ShowLoading(true)
            }

            Thread.sleep(2000) // todo: call API

            setEffect {
                KycBaseIdUploadContract.Effect.ShowLoading(false)
            }

            setEffect {
                if (currentState.imageUri == null) {
                    KycBaseIdUploadContract.Effect.ShowSnackBar(
                        getString(R.string.KycIdUpload_DefaultErrorMessage)
                    )
                } else {
                    KycBaseIdUploadContract.Effect.GoToNextStep
                }
            }
        }
    }

    internal fun hasBackCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    internal fun hasFrontCamera(cameraProvider: ProcessCameraProvider?): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }
}