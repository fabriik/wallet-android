package com.fabriik.kyc.ui.features.personalinformation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(
    application
) {

    override fun createInitialState() = PersonalInformationContract.State()

    override fun handleEvent(event: PersonalInformationContract.Event) {
        when (event) {
            is PersonalInformationContract.Event.BackClicked ->
                setEffect { PersonalInformationContract.Effect.GoBack }

            is PersonalInformationContract.Event.DismissClicked ->
                setEffect { PersonalInformationContract.Effect.Dismiss }

            is PersonalInformationContract.Event.ConfirmClicked ->
                setEffect { PersonalInformationContract.Effect.GoToExposedPerson }

            is PersonalInformationContract.Event.ScreenInfoClicked -> {} //todo:

            is PersonalInformationContract.Event.ExposedPersonInfoClicked -> {} //todo:

            is PersonalInformationContract.Event.NameChanged ->
                setState { copy(name = event.name).validate() }

            is PersonalInformationContract.Event.LastNameChanged ->
                setState { copy(lastName = event.lastName).validate() }

            is PersonalInformationContract.Event.CountryChanged ->
                setState { copy(country = event.country).validate() }

            is PersonalInformationContract.Event.ExposedPersonChanged ->
                setState { copy(exposedPerson = event.exposedPerson).validate() }
        }
    }

    private fun PersonalInformationContract.State.validate() = copy(
        confirmEnabled = exposedPerson != null && name.isNotBlank() && lastName.isNotBlank()
                && country.isNotBlank()
    )
}