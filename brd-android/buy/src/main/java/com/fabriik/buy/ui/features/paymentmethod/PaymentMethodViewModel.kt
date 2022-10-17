package com.fabriik.buy.ui.features.paymentmethod

import android.app.Application
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs
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
                PaymentMethodContract.Effect.Back(
                    PaymentMethodFragment.Result.Cancelled(currentState.dataUpdated)
                )
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

    override fun onRemoveOptionClicked(paymentInstrument: PaymentInstrument) {
        setEffect {
            PaymentMethodContract.Effect.ShowConfirmationDialog(
                FabriikGenericDialogArgs(
                    requestKey = REQUEST_CONFIRMATION_DIALOG,
                    title = getString(R.string.Buy_RemoveCard_android, paymentInstrument.last4Numbers),
                    positive = FabriikGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_remove,
                        resultKey = RESULT_CONFIRMATION_DIALOG_REMOVE
                    ),
                    negative = FabriikGenericDialogArgs.ButtonData(
                        titleRes = R.string.Button_cancel,
                        resultKey = RESULT_CONFIRMATION_DIALOG_CANCEL
                    ),
                    extraData = bundleOf(EXTRA_CONFIRMATION_DIALOG_DATA to paymentInstrument),
                    showDismissButton = true
                )
            )
        }
    }

    override fun onPaymentInstrumentClicked(paymentInstrument: PaymentInstrument) {
        if (arguments.flow == AddCardFlow.BUY) {
            setEffect {
                PaymentMethodContract.Effect.Back(
                    PaymentMethodFragment.Result.Selected(paymentInstrument)
                )
            }
        }
    }

    override fun onPaymentInstrumentOptionsClicked(paymentInstrument: PaymentInstrument) {
        setEffect { PaymentMethodContract.Effect.ShowOptionsBottomSheet(paymentInstrument) }
    }

    override fun onPaymentInstrumentRemovalConfirmed(paymentInstrument: PaymentInstrument) {
        callApi(
            endState = { copy(fullScreenLoadingIndicator = false) },
            startState = { copy(fullScreenLoadingIndicator = true) },
            action = { buyApi.deletePaymentInstrument(paymentInstrument) },
            callback = {
                val updatedList = currentState.paymentInstruments.toMutableList().apply {
                    remove(paymentInstrument)
                }

                setState {
                    copy(
                        paymentInstruments = updatedList,
                        dataUpdated = true
                    )
                }

                setEffect {
                    if (it.status == Status.SUCCESS) {
                        PaymentMethodContract.Effect.ShowToast(
                            getString(R.string.Buy_CardRemoved)
                        )
                    } else {
                        PaymentMethodContract.Effect.ShowError(
                            getString(R.string.Buy_CardRemovalFailed)
                        )
                    }
                }
            }
        )
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
                                it.message ?: getString(R.string.Api_DefaultError)
                            )
                        }
                }
            }
        )
    }

    companion object {
        const val EXTRA_CONFIRMATION_DIALOG_DATA = "extra_confirmation_dialog_data"
        const val REQUEST_CONFIRMATION_DIALOG = "request_confirmation_dialog"
        const val RESULT_CONFIRMATION_DIALOG_REMOVE = "result_confirmation_dialog_remove"
        const val RESULT_CONFIRMATION_DIALOG_CANCEL = "result_confirmation_dialog_cancel"
    }
}