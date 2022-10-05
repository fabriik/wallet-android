package com.fabriik.buy.ui.features.addcard

import com.fabriik.common.ui.base.FabriikContract

class AddCardContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object SecurityCodeInfoClicked : Event()

        data class OnCardNumberChanged(
            val number: String
        ) : Event()

        data class OnDateChanged(
            val date: String
        ) : Event()

        data class OnSecurityCodeChanged(
            val code: String
        ) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object ShowCvvInfoDialog : Effect()

        data class BillingAddress(
            val token: String,
            val flow: AddCardFlow
        ) : Effect()

        data class ShowError(
            val message: String
        ) : Effect()
    }

    data class State(
        val expiryDate: String = "",
        val cardNumber: String = "",
        val securityCode: String = "",
        val confirmButtonEnabled: Boolean = false,
        val loadingIndicatorVisible: Boolean = false
    ) : FabriikContract.State
}