package com.fabriik.buy.ui.features.paymentmethod

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle

class PaymentMethodViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<PaymentMethodContract.State, PaymentMethodContract.Event, PaymentMethodContract.Effect>(
    application, savedStateHandle
) {
    private lateinit var arguments: PaymentMethodFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = PaymentMethodFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentMethodContract.State(
        paymentInstruments = arguments.paymentInstruments.toList()
    )

    override fun handleEvent(event: PaymentMethodContract.Event) {
        when (event) {
            PaymentMethodContract.Event.OnBackClicked ->
                setEffect { PaymentMethodContract.Effect.Back }

            PaymentMethodContract.Event.OnDismissClicked ->
                setEffect { PaymentMethodContract.Effect.Dismiss }
        }
    }
}