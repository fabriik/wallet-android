package com.fabriik.buy.ui.features.paymentmethod

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PaymentMethodViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<PaymentMethodContract.State, PaymentMethodContract.Event, PaymentMethodContract.Effect>(
    application, savedStateHandle
), PaymentMethodEventHandler, KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private lateinit var arguments: PaymentMethodFragmentArgs

    init {
        loadInitialData()
    }

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        super.parseArguments(savedStateHandle)

        arguments = PaymentMethodFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentMethodContract.State(
        paymentInstruments = emptyList(),
        showDismissButton = arguments.flow == AddCardFlow.BUY
    )

    override fun onBackClicked() {
        setEffect {
            if (arguments.flow == AddCardFlow.BUY) {
                PaymentMethodContract.Effect.Back()
            } else {
                PaymentMethodContract.Effect.Dismiss
            }
        }
    }

    override fun onDismissClicked() {
        setEffect { PaymentMethodContract.Effect.Dismiss }
    }

    override fun onAddCardClicked() {
        setEffect { PaymentMethodContract.Effect.AddCard(arguments.flow) }
    }

    override fun onPaymentInstrumentSelected(paymentInstrument: PaymentInstrument) {
        if (arguments.flow == AddCardFlow.BUY) {
            setEffect { PaymentMethodContract.Effect.Back(paymentInstrument) }
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