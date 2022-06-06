package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle

class SubmitPhotoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle

) : FabriikViewModel<SubmitPhotoContract.State, SubmitPhotoContract.Event, SubmitPhotoContract.Effect>(
    application, savedStateHandle
) {
    lateinit var arguments: SubmitPhotoFragmentArgs
    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SubmitPhotoFragmentArgs.fromBundle(savedStateHandle.toBundle())
        super.parseArguments(savedStateHandle)
    }

    override fun createInitialState() = SubmitPhotoContract.State(
        documentType = arguments.documentType,
        image = arguments.imageUri,
    )

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        when (event) {
            is SubmitPhotoContract.Event.BackClicked ->
                setEffect { SubmitPhotoContract.Effect.Back }
            is SubmitPhotoContract.Event.DismissClicked ->
                setEffect { SubmitPhotoContract.Effect.Dismiss }
            is SubmitPhotoContract.Event.RetakeClicked ->
                setEffect { SubmitPhotoContract.Effect.TakePhoto }
            is SubmitPhotoContract.Event.ConfirmClicked ->
                setEffect { SubmitPhotoContract.Effect.PostValidation }
        }
    }
}