package com.fabriik.kyc.ui.features.personalinformation

import com.fabriik.common.ui.base.FabriikEventHandler
import com.fabriik.kyc.data.model.Country

interface PersonalInformationEventHandler: FabriikEventHandler<PersonalInformationContract.Event> {

    override fun handleEvent(event: PersonalInformationContract.Event) {
        return when (event) {
            is PersonalInformationContract.Event.LoadProfile -> onLoadProfile()
            is PersonalInformationContract.Event.BackClicked -> onBackClicked()
            is PersonalInformationContract.Event.DateClicked -> onDateClicked()
            is PersonalInformationContract.Event.CountryClicked -> onCountryClicked()
            is PersonalInformationContract.Event.ConfirmClicked -> onConfirmClicked()
            is PersonalInformationContract.Event.DismissClicked -> onDismissClicked()
            is PersonalInformationContract.Event.NameChanged -> onNameChanged(event.name)
            is PersonalInformationContract.Event.DateChanged -> onDateChanged(event.date)
            is PersonalInformationContract.Event.LastNameChanged -> onLastNameChanged(event.lastName)
            is PersonalInformationContract.Event.CountryChanged -> onCountryChanged(event.country)
        }
    }

    fun onBackClicked()

    fun onDateClicked()

    fun onCountryClicked()

    fun onConfirmClicked()

    fun onDismissClicked()

    fun onLoadProfile()

    fun onNameChanged(name: String)

    fun onDateChanged(date: Long)

    fun onLastNameChanged(lastName: String)

    fun onCountryChanged(country: Country)
}