package com.fabriik.kyc.ui.features.prevalidation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PreValidationViewModel(
    application: Application

) : FabriikViewModel<PreValidationContract.State, PreValidationContract.Event, PreValidationContract.Effect>(
    application
), PreValidationEventHandler {
    override fun createInitialState() = PreValidationContract.State

    override fun onBackClicked() {
        setEffect { PreValidationContract.Effect.Back }
    }

    override fun onConfirmClicked() {
        setEffect { PreValidationContract.Effect.ProofOfIdentity }
    }

    override fun onDismissCLicked() {
        setEffect { PreValidationContract.Effect.Dismiss }
    }
}