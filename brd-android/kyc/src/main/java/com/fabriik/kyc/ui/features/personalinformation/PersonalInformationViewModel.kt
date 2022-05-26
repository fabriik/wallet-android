package com.fabriik.kyc.ui.features.personalinformation

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.TextValidator

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(
    application
) {

    private val textValidator = TextValidator

    override fun createInitialState() = PersonalInformationContract.State()

    override fun handleEvent(event: PersonalInformationContract.Event) {
        when (event) {
            is PersonalInformationContract.Event.BackClicked ->
                setEffect { PersonalInformationContract.Effect.GoBack }

            is PersonalInformationContract.Event.DismissClicked ->
                setEffect { PersonalInformationContract.Effect.Dismiss }

            is PersonalInformationContract.Event.ConfirmClicked ->
                // todo: call API
                setEffect {
                    if (currentState.exposedPerson == true) {
                        PersonalInformationContract.Effect.GoToExposedPerson
                    } else {
                        PersonalInformationContract.Effect.GoToAccountVerification
                    }
                }

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
        confirmEnabled = exposedPerson != null
                && country != null
                && textValidator(name)
                && textValidator(lastName)
    )
}