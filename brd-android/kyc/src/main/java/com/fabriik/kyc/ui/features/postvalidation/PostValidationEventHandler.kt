package com.fabriik.kyc.ui.features.postvalidation

import com.fabriik.common.ui.base.FabriikEventHandler

interface PostValidationEventHandler: FabriikEventHandler<PostValidationContract.Event> {

    override fun handleEvent(event: PostValidationContract.Event) {
        return when (event) {
            is PostValidationContract.Event.ConfirmClicked -> onConfirmClicked()
        }
    }

    fun onConfirmClicked()
}