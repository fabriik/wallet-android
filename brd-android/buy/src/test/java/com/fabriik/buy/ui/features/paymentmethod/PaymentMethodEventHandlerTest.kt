package com.fabriik.buy.ui.features.paymentmethod
import com.fabriik.common.data.model.PaymentInstrument
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentMethodEventHandlerTest {

    @Mock lateinit var paymentInstrument: PaymentInstrument

    @Spy val handler = object : PaymentMethodEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onAddCardClicked() {}
        override fun onRemoveOptionClicked(paymentInstrument: PaymentInstrument) {}
        override fun onPaymentInstrumentClicked(paymentInstrument: PaymentInstrument) {}
        override fun onPaymentInstrumentOptionsClicked(paymentInstrument: PaymentInstrument) {}
        override fun onPaymentInstrumentRemovalConfirmed(paymentInstrument: PaymentInstrument) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(PaymentMethodContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(PaymentMethodContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_addCardClicked_callOnAddCardClicked() {
        handler.handleEvent(PaymentMethodContract.Event.AddCardClicked)
        verify(handler).onAddCardClicked()
    }

    @Test
    fun handleEvent_paymentInstrumentClicked_callOnPaymentInstrumentClicked() {
        handler.handleEvent(PaymentMethodContract.Event.PaymentInstrumentClicked(paymentInstrument))
        verify(handler).onPaymentInstrumentClicked(paymentInstrument)
    }

    @Test
    fun handleEvent_paymentInstrumentOptionsClicked_callOnPaymentInstrumentOptionsClicked() {
        handler.handleEvent(PaymentMethodContract.Event.PaymentInstrumentOptionsClicked(paymentInstrument))
        verify(handler).onPaymentInstrumentOptionsClicked(paymentInstrument)
    }

    @Test
    fun handleEvent_removeOptionClicked_callOnRemoveOptionClicked() {
        handler.handleEvent(PaymentMethodContract.Event.RemoveOptionClicked(paymentInstrument))
        verify(handler).onRemoveOptionClicked(paymentInstrument)
    }

    @Test
    fun handleEvent_paymentInstrumentRemovalConfirmed_callOnPaymentInstrumentRemovalConfirmed() {
        handler.handleEvent(PaymentMethodContract.Event.PaymentInstrumentRemovalConfirmed(paymentInstrument))
        verify(handler).onPaymentInstrumentRemovalConfirmed(paymentInstrument)
    }
}