package com.fabriik.kyc.ui.features.addressinformation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.TextValidator

class AddressInformationViewModel(
    application: Application
) : FabriikViewModel<AddressInformationContract.State, AddressInformationContract.Event, AddressInformationContract.Effect>(
    application
) {

    override fun createInitialState() = AddressInformationContract.State()

    override fun handleEvent(event: AddressInformationContract.Event) {
        when (event) {
            is AddressInformationContract.Event.BackClicked ->
                setEffect { AddressInformationContract.Effect.GoBack }

            is AddressInformationContract.Event.DismissClicked ->
                setEffect { AddressInformationContract.Effect.Dismiss }

            is AddressInformationContract.Event.ConfirmClicked ->
                setEffect { AddressInformationContract.Effect.GoToProofOfResidence }

            is AddressInformationContract.Event.ZipChanged ->
                setState { copy(zip = event.zip).validate() }

            is AddressInformationContract.Event.CityChanged ->
                setState { copy(city = event.city).validate() }

            is AddressInformationContract.Event.StateChanged ->
                setState { copy(state = event.state).validate() }

            is AddressInformationContract.Event.CountryChanged ->
                setState { copy(country = event.country).validate() }

            is AddressInformationContract.Event.AddressLine1Changed ->
                setState { copy(addressLine1 = event.addressLine1).validate() }

            is AddressInformationContract.Event.AddressLine2Changed ->
                setState { copy(addressLine2 = event.addressLine2).validate() }
        }
    }

    private fun AddressInformationContract.State.validate() = copy(
        confirmEnabled = TextValidator(country) && TextValidator(state) && TextValidator(city)
                && TextValidator(zip) && TextValidator(addressLine1)
    )
}