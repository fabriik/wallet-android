package com.fabriik.kyc.ui.features.proofofresidence

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class ProofOfResidenceViewModel(
    application: Application
) : FabriikViewModel<ProofOfResidenceContract.State, ProofOfResidenceContract.Event, ProofOfResidenceContract.Effect>(application) {

    override fun createInitialState() = ProofOfResidenceContract.State()

    override fun handleEvent(event: ProofOfResidenceContract.Event) {
        when (event) {
            is ProofOfResidenceContract.Event.BackClicked ->
                setEffect {
                    ProofOfResidenceContract.Effect.GoBack
                }

            is ProofOfResidenceContract.Event.DismissClicked ->
                setEffect {
                    ProofOfResidenceContract.Effect.Dismiss
                }

            is ProofOfResidenceContract.Event.NoClicked ->
                setEffect {
                    ProofOfResidenceContract.Effect.GoToProofUpload
                }
        }
    }
}