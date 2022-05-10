package com.fabriik.kyc.ui.features.proofofresidenceupload

import com.fabriik.common.ui.base.FabriikContract

interface UploadProofOfResidenceContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object InfoClicked : Event()
        object DismissClicked : Event()
        object SubmitClicked : Event()
        object AddDocumentClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToCompleted : Effect()
        class OpenPhotoSourcePicker(val requestKey: String) : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}