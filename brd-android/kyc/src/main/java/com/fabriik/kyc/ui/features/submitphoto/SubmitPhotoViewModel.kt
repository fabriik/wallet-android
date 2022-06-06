package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.kyc.data.enums.DocumentSide
import com.fabriik.kyc.data.enums.DocumentType

class SubmitPhotoViewModel(
    application: Application

) : FabriikViewModel<SubmitPhotoContract.State, SubmitPhotoContract.Event, SubmitPhotoContract.Effect>(
    application
) {

    override fun createInitialState() = SubmitPhotoContract.State()

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        when (event) {
            is SubmitPhotoContract.Event.BackClicked,
            is SubmitPhotoContract.Event.RetakeClicked ->
                setEffect { SubmitPhotoContract.Effect.Back }

            is SubmitPhotoContract.Event.DismissClicked ->
                setEffect { SubmitPhotoContract.Effect.Dismiss }

            is SubmitPhotoContract.Event.ConfirmClicked ->
                onConfirmClicked()

            is SubmitPhotoContract.Event.OnCreate ->
                setState { copy(documentType = event.documentType, documentSide = event.documentSide, image = event.image) }
        }
    }

    private fun onConfirmClicked() {
        setEffect {
            when (currentState.documentType!!) {
                DocumentType.SELFIE ->
                    SubmitPhotoContract.Effect.PostValidation

                DocumentType.PASSPORT ->
                    SubmitPhotoContract.Effect.TakePhoto(
                        documentType = DocumentType.SELFIE,
                        documentSide = DocumentSide.FRONT
                    )

                else ->
                    when (currentState.documentSide!!) {
                        DocumentSide.FRONT ->
                            SubmitPhotoContract.Effect.TakePhoto(
                                documentType = currentState.documentType!!,
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