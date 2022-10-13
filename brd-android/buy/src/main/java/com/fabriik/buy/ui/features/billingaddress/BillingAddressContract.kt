package com.fabriik.buy.ui.features.billingaddress

import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

class BillingAddressContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackPressed : Event()
        object DismissClicked: Event()
        object CountryClicked: Event()
        object ConfirmClicked: Event()
        data class ZipChanged(val zip: String): Event()
        data class CityChanged(val city: String): Event()
        data class StateChanged(val state: String): Event()
        data class AddressChanged(val address: String): Event()
        data class LastNameChanged(val lastName: String): Event()
        data class FirstNameChanged(val firstName: String): Event()
        data class CountryChanged(val country: Country): Event()
        data class PaymentChallengeResult(val result: Int): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object CountrySelection : Effect()
        data class PaymentMethod(val flow: AddCardFlow) : Effect()
        data class ShowError(val message: String): Effect()
        data class OpenWebsite(val url: String): Effect()
    }

    data class State(
        val firstName: String = "",
        val lastName: String = "",
        val zip: String = "",
        val city: String = "",
        val state: String? = null,
        val address: String = "",
        val country: Country? = null,
        val confirmEnabled: Boolean = false,
        val loadingIndicatorVisible: Boolean = false,
        val paymentReference: String? = null,
    ) : FabriikContract.State
}