package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs

interface PaymentMethodContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object AddCardClicked : Event()
        data class RemoveOptionClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentOptionsClicked(val paymentInstrument: PaymentInstrument): Event()
        data class PaymentInstrumentRemovalConfirmed(val paymentInstrument: PaymentInstrument): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class AddCard(val flow: AddCardFlow) : Effect()
        data class Back(val result: PaymentMethodFragment.Result) : Effect()
        data class ShowError(val message: String) : Effect()
        data class ShowToast(val message: String): Effect()
        data class ShowOptionsBottomSheet(val paymentInstrument: PaymentInstrument) : Effect()
        data class ShowConfirmationDialog(val args: FabriikGenericDialogArgs) : Effect()
    }

    data class State(
        val dataUpdated: Boolean = false,
        val paymentInstruments: List<PaymentInstrument>,
        val initialLoadingIndicator: Boolean = false,
        val fullScreenLoadingIndicator: Boolean = false,
        val showDismissButton: Boolean = false
    ) : FabriikContract.State
}