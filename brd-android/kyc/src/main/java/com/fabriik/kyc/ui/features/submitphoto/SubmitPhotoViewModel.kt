package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType
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
        documentSide = arguments.documentSide,
        image = arguments.imageUri,
    )

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        when (event) {
            is SubmitPhotoContract.Event.BackClicked,
            is SubmitPhotoContract.Event.RetakeClicked ->
                setEffect { SubmitPhotoContract.Effect.Back }

            is SubmitPhotoContract.Event.DismissClicked ->
                setEffect { SubmitPhotoContract.Effect.Dismiss }

            is SubmitPhotoContract.Event.ConfirmClicked ->
                onConfirmClicked()
        }
    }

    private fun onConfirmClicked() {
        setEffect {
            when (currentState.documentType) {
                DocumentType.SELFIE ->
                    SubmitPhotoContract.Effect.PostValidation

                DocumentType.PASSPORT ->
                    SubmitPhotoContract.Effect.TakePhoto(
                        documentType = DocumentType.SELFIE,
                        documentSide = DocumentSide.FRONT
                    )

                else ->
                    when (currentState.documentSide) {
                        DocumentSide.FRONT ->
                            SubmitPhotoContract.Effect.TakePhoto(
                                documentType = currentState.documentType,
                                documentSide = DocumentSide.BACK
                            )

                        DocumentSide.BACK ->
                            SubmitPhotoContract.Effect.TakePhoto(
                                documentType = DocumentType.SELFIE,
                                documentSide = DocumentSide.FRONT
                            )
                    }
            }
        }
    }
}