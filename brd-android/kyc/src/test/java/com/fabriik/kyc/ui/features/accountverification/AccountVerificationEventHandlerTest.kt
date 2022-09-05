package com.fabriik.kyc.ui.features.accountverification

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AccountVerificationEventHandlerTest {

    @Spy val handler = object : AccountVerificationEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onInfoClicked() {}
        override fun onLevel1Clicked() {}
        override fun onLevel2Clicked() {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(AccountVerificationContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(AccountVerificationContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_infoClicked_callOnInfoClicked() {
        handler.handleEvent(AccountVerificationContract.Event.InfoClicked)
        verify(handler).onInfoClicked()
    }

    @Test
    fun handleEvent_level1Clicked_callOnLevel1Clicked() {
        handler.handleEvent(AccountVerificationContract.Event.Level1Clicked)
        verify(handler).onLevel1Clicked()
    }

    @Test
    fun handleEvent_level2Clicked_callOnLevel2Clicked() {
        handler.handleEvent(AccountVerificationContract.Event.Level2Clicked)
        verify(handler).onLevel2Clicked()
    }
}