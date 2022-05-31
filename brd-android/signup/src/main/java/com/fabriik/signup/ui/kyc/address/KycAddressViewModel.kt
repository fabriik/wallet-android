package com.fabriik.signup.ui.kyc.address

import android.app.Application
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.signup.R
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.signup.utils.getString

class KycAddressViewModel(
    application: Application
) : FabriikViewModel<KycAddressContract.State, KycAddressContract.Event, KycAddressContract.Effect>(
    application
) {

    override fun createInitialState() = KycAddressContract.State()

    override fun handleEvent(event: KycAddressContract.Event) {
        when (event) {
            is KycAddressContract.Event.CountryChanged ->
                setState {
                    copy(
                        country = event.country,
                        countryValid = event.country.isNotBlank()
                    )
                }

            is KycAddressContract.Event.ZipCodeChanged ->
                setState {
                    copy(
                        zipCode = event.zipCode,
                        zipCodeValid = event.zipCode.isNotBlank()
                    )
                }

            is KycAddressContract.Event.AddressStreetChanged ->
                setState {
                    copy(
                        addressStreet = event.street,
                        addressStreetValid = event.street.isNotBlank()
                    )
                }

            is KycAddressContract.Event.AddressUnitChanged ->
                setState {
                    copy(
                        addressUnit = event.unit,
                        addressUnitValid = event.unit.isNotBlank()
                    )
                }

            is KycAddressContract.Event.CityChanged ->
                setState {
                    copy(
                        city = event.city,
                        cityValid = event.city.isNotBlank()
                    )
                }

            is KycAddressContract.Event.StateChanged ->
                setState {
                    copy(
                        state = event.state,
                        stateValid = event.state.isNotBlank()
                    )
                }

            is KycAddressContract.Event.NextClicked ->
                validateAddressData()

            is KycAddressContract.Event.PrivacyPolicyClicked ->
                setEffect {
                    KycAddressContract.Effect.OpenWebsite(
                        FabriikApiConstants.URL_PRIVACY_POLICY
                    )
                }

            is KycAddressContract.Event.TermsAndConditionsClicked ->
                setEffect {
                    KycAddressContract.Effect.OpenWebsite(
                        FabriikApiConstants.URL_TERMS_AND_CONDITIONS
                    )
                }
        }
    }

    private fun validateAddressData() {
        val validData = currentState.addressStreetValid && currentState.cityValid
                && currentState.zipCodeValid && currentState.countryValid

        if (validData) {
            setEffect {
                KycAddressContract.Effect.GoToPersonalInfo(
                    zip = currentState.zipCode,
                    city = currentState.city,
                    state = currentState.state,
                    street = "${currentState.addressStreet}, ${currentState.addressUnit}",
                    country = currentState.country
                )
            }
        } else {
            setEffect {
                KycAddressContract.Effect.ShowSnackBar(
                    getString(R.string.ResetPassword_EnterValidData)
                )
            }
        }
    }
}