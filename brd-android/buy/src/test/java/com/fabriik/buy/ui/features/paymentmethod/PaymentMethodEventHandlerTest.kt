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
        override fun onPaymentInstrumentSelected(paymentInstrument: PaymentInstrument) {}
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
    fun handleEvent_paymentInstrumentSelected_callOnPaymentInstrumentSelected() {
        handler.handleEvent(PaymentMethodContract.Event.PaymentInstrumentSelected(paymentInstrument))
        verify(handler).onPaymentInstrumentSelected(paymentInstrument)
    }
}