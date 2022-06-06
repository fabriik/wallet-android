package com.fabriik.kyc.ui.features.submitphoto

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SubmitPhotoViewModel(
    application: Application

) : FabriikViewModel<SubmitPhotoContract.State, SubmitPhotoContract.Event, SubmitPhotoContract.Effect>(
    application
) {

    override fun createInitialState() = SubmitPhotoContract.State()

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        when (event) {
            is SubmitPhotoContract.Event.BackClicked ->
                setEffect { SubmitPhotoContract.Effect.GoBack }
            is SubmitPhotoContract.Event.RetakeClicked ->
                setEffect { SubmitPhotoContract.Effect.GoToCamera }
            is SubmitPhotoContract.Event.ConfirmClicked ->
                setEffect { SubmitPhotoContract.Effect.GoForward }
            is SubmitPhotoContract.Event.OnCreate ->
                setState { copy(documentType = event.documentType, image = event.image) }
        }
    }
}