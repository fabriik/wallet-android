package com.fabriik.kyc.ui.features.proofofidentity

import com.fabriik.common.ui.base.FabriikEventHandler

interface ProofOfIdentityEventHandler: FabriikEventHandler<ProofOfIdentityContract.Event> {

    override fun handleEvent(event: ProofOfIdentityContract.Event) {
        return when (event) {
            is ProofOfIdentityContract.Event.LoadDocuments -> onLoadDocuments()
            is ProofOfIdentityContract.Event.BackClicked -> onBackClicked()
            is ProofOfIdentityContract.Event.DismissClicked -> onDismissClicked()
            is ProofOfIdentityContract.Event.IdCardClicked -> onIdCardClicked()
            is ProofOfIdentityContract.Event.PassportClicked -> onPassportClicked()
            is ProofOfIdentityContract.Event.DrivingLicenceClicked -> onDrivingLicenceClicked()
            is ProofOfIdentityContract.Event.ResidencePermitClicked -> onResidencePermitClicked()
        }
    }

    fun onLoadDocuments()

    fun onBackClicked()

    fun onDismissClicked()

    fun onIdCardClicked()

    fun onPassportClicked()

    fun onDrivingLicenceClicked()

    fun onResidencePermitClicked()
}