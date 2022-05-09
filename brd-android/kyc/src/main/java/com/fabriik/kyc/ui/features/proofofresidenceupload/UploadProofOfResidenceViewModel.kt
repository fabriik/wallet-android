package com.fabriik.kyc.ui.features.proofofresidenceupload

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class UploadProofOfResidenceViewModel(
    application: Application
) : FabriikViewModel<UploadProofOfResidenceContract.State, UploadProofOfResidenceContract.Event, UploadProofOfResidenceContract.Effect>(application) {

    override fun createInitialState() = UploadProofOfResidenceContract.State()

    override fun handleEvent(event: UploadProofOfResidenceContract.Event) {
        when (event) {
            is UploadProofOfResidenceContract.Event.BackClicked ->
                setEffect {
                    UploadProofOfResidenceContract.Effect.GoBack
                }

            is UploadProofOfResidenceContract.Event.DismissClicked ->
                setEffect {
                    UploadProofOfResidenceContract.Effect.Dismiss
                }

            is UploadProofOfResidenceContract.Event.SubmitClicked ->
                setEffect {
                    UploadProofOfResidenceContract.Effect.GoToCompleted
                }
        }
    }
}