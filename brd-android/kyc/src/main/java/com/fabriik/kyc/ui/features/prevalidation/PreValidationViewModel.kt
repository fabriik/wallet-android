package com.fabriik.kyc.ui.features.prevalidation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PreValidationViewModel(
    application: Application

) : FabriikViewModel<PreValidationContract.State, PreValidationContract.Event, PreValidationContract.Effect>(
    application
) {
    override fun createInitialState() = PreValidationContract.State

    override fun handleEvent(event: PreValidationContract.Event) {
        when (event) {
            is PreValidationContract.Event.BackClicked ->
                setEffect { PreValidationContract.Effect.GoBack }
            is PreValidationContract.Event.ConfirmClicked ->
                setEffect { PreValidationContract.Effect.GoForward }
        }
    }
}