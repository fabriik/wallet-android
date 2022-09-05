package com.fabriik.buy.ui.features.paymentmethod
import com.fabriik.common.data.model.PaymentInstrument
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentMethodEventHandlerTest {

    @Spy val handler = object : PaymentMethodEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onAddCardClicked() {}
        override fun onPaymentInstrumentSelected(paymentInstrument: PaymentInstrument) {}
    }
}