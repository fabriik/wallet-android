package com.fabriik.kyc.ui.features.prevalidation

import com.fabriik.common.ui.base.FabriikEventHandler

interface PreValidationEventHandler: FabriikEventHandler<PreValidationContract.Event> {

    override fun handleEvent(event: PreValidationContract.Event) {
        return when (event) {
            is PreValidationContract.Event.BackClicked -> onBackClicked()
            is PreValidationContract.Event.ConfirmClicked -> onConfirmClicked()
            is PreValidationContract.Event.DismissCLicked -> onDismissCLicked()
        }
    }

    fun onBackClicked()

    fun onConfirmClicked()

    fun onDismissCLicked()
}