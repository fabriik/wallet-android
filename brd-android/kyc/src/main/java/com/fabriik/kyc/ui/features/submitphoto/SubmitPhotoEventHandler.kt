package com.fabriik.kyc.ui.features.submitphoto

import com.fabriik.common.ui.base.FabriikEventHandler

interface SubmitPhotoEventHandler: FabriikEventHandler<SubmitPhotoContract.Event> {

    override fun handleEvent(event: SubmitPhotoContract.Event) {
        return when (event) {
            is SubmitPhotoContract.Event.BackClicked -> onBackClicked()
            is SubmitPhotoContract.Event.RetakeClicked -> onRetakeClicked()
            is SubmitPhotoContract.Event.ConfirmClicked -> onConfirmClicked()
            is SubmitPhotoContract.Event.DismissClicked -> onDismissClicked()
        }
    }

    fun onBackClicked()

    fun onRetakeClicked()

    fun onConfirmClicked()

    fun onDismissClicked()
}