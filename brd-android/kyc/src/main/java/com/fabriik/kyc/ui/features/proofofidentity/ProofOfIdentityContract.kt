package com.fabriik.kyc.ui.features.proofofidentity

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.enums.DocumentType

interface ProofOfIdentityContract {

    sealed class Event : FabriikContract.Event {
        object LoadDocuments : Event()
        object BackClicked : Event()
        object IdCardClicked : Event()
        object PassportClicked : Event()
        object DrivingLicenceClicked : Event()
        object ResidencePermitClicked : Event()
        object DismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        data class ShowToast(val message: String) : Effect()
        data class GoToDocumentUpload(val documentType: DocumentType) : Effect()
    }

    data class State(
        val idCardVisible: Boolean = false,
        val passportVisible: Boolean = false,
        val drivingLicenceVisible: Boolean = false,
        val residencePermitVisible: Boolean = false,
        val initialLoadingVisible: Boolean = false
    ) : FabriikContract.State
}