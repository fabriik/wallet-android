package com.fabriik.kyc.ui.features.proofofidentity

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentType

interface ProofOfIdentityContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object InfoClicked : Event()
        object DismissClicked : Event()
        object IdCardClicked : Event()
        object PassportClicked : Event()
        object DrivingLicenceClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        class GoToDocumentUpload(val documentType: DocumentType) : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}