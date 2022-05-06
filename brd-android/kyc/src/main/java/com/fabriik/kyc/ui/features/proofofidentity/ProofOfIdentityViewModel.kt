package com.fabriik.kyc.ui.features.proofofidentity

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class ProofOfIdentityViewModel(
    application: Application
) : FabriikViewModel<ProofOfIdentityContract.State, ProofOfIdentityContract.Event, ProofOfIdentityContract.Effect>(application) {

    override fun createInitialState() = ProofOfIdentityContract.State()

    override fun handleEvent(event: ProofOfIdentityContract.Event) {
        when (event) {
            is ProofOfIdentityContract.Event.BackClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.GoBack
                }

            is ProofOfIdentityContract.Event.DismissClicked ->
                setEffect {
                    ProofOfIdentityContract.Effect.Dismiss
                }
        }
    }
}