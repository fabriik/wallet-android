package com.fabriik.buy.ui.features.paymentmethod

import android.app.Application
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PaymentMethodViewModel(
    application: Application,
) : FabriikViewModel<PaymentMethodContract.State, PaymentMethodContract.Event, PaymentMethodContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    init {
        loadInitialData()
    }

    override fun createInitialState() = PaymentMethodContract.State(
        paymentInstruments = emptyList()
    )

    override fun handleEvent(event: PaymentMethodContract.Event) {
        when (event) {
            PaymentMethodContract.Event.BackClicked ->
                setEffect { PaymentMethodContract.Effect.Back() }

            PaymentMethodContract.Event.DismissClicked ->
                setEffect { PaymentMethodContract.Effect.Dismiss }

            PaymentMethodContract.Event.AddCardClicked ->
                setEffect { PaymentMethodContract.Effect.AddCard }

            is PaymentMethodContract.Event.PaymentInstrumentSelected ->
                setEffect { PaymentMethodContract.Effect.Back(event.paymentInstrument) }
        }
    }

    private fun loadInitialData() {
        callApi(
            endState = { copy(initialLoadingIndicator = false) },
            startState = { copy(initialLoadingIndicator = true) },
            action = { buyApi.getPaymentInstruments() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState { copy(paymentInstruments = requireNotNull(it.data)) }

                    Status.ERROR ->
                        setEffect {
                            PaymentMethodContract.Effect.ShowError(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
            }
        )
    }
}