package com.fabriik.kyc.ui.features.proofofidentity

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProofOfIdentityEventHandlerTest {

    @Spy val handler = object : ProofOfIdentityEventHandler {
        override fun onLoadDocuments() {}
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onIdCardClicked() {}
        override fun onPassportClicked() {}
        override fun onDrivingLicenceClicked() {}
        override fun onResidencePermitClicked() {}
    }

    @Test
    fun handleEvent_loadDocuments_callOnLoadDocuments() {
        handler.handleEvent(ProofOfIdentityContract.Event.LoadDocuments)
        verify(handler).onLoadDocuments()
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_idCardClicked_callOnIdCardClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.IdCardClicked)
        verify(handler).onIdCardClicked()
    }

    @Test
    fun handleEvent_passportClicked_callOnPassportClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.PassportClicked)
        verify(handler).onPassportClicked()
    }

    @Test
    fun handleEvent_drivingLicenceClicked_callOnDrivingLicenceClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.DrivingLicenceClicked)
        verify(handler).onDrivingLicenceClicked()
    }

    @Test
    fun handleEvent_residencePermitClicked_callOnResidencePermitClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.ResidencePermitClicked)
        verify(handler).onResidencePermitClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(ProofOfIdentityContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }
}