package com.fabriik.signup.ui.kyc.address

import com.fabriik.common.ui.base.FabriikContract

interface KycAddressContract {

    sealed class Event : FabriikContract.Event {
        object NextClicked: Event()
        object PrivacyPolicyClicked: Event()
        object TermsAndConditionsClicked: Event()
        class CityChanged(val city: String): Event()
        class StateChanged(val state: String): Event()
        class CountryChanged(val country: String): Event()
        class ZipCodeChanged(val zipCode: String): Event()
        class AddressUnitChanged(val unit: String): Event()
        class AddressStreetChanged(val street: String): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        class GoToPersonalInfo(
            val street: String,
            val city: String,
            val state: String,
            val zip: String,
            val country: String
        ) : Effect()
        class OpenWebsite(val url: String) : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
    }

    data class State(
        val country: String = "",
        val countryValid: Boolean = false,
        val zipCode: String = "",
        val zipCodeValid: Boolean = false,
        val addressStreet: String = "",
        val addressStreetValid: Boolean = false,
        val addressUnit: String = "",
        val addressUnitValid: Boolean = false,
        val city: String = "",
        val cityValid: Boolean = false,
        val state: String = "",
        val stateValid: Boolean = false
    ): FabriikContract.State
}