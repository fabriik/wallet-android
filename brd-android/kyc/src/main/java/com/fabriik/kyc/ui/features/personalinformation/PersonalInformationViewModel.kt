package com.fabriik.kyc.ui.features.personalinformation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.TextValidator
import com.fabriik.kyc.data.KycApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(
    application
) {

    private val kycApi = KycApi.create()
    private val textValidator = TextValidator

    override fun createInitialState() = PersonalInformationContract.State()

    override fun handleEvent(event: PersonalInformationContract.Event) {
        when (event) {
            is PersonalInformationContract.Event.BackClicked ->
                setEffect { PersonalInformationContract.Effect.GoBack }

            is PersonalInformationContract.Event.DismissClicked ->
                setEffect { PersonalInformationContract.Effect.Dismiss }

            is PersonalInformationContract.Event.CountryClicked ->
                setEffect { PersonalInformationContract.Effect.CountrySelection }

            PersonalInformationContract.Event.DateClicked ->
                setEffect {
                    PersonalInformationContract.Effect.DateSelection(currentState.dateOfBirth)
                }

            is PersonalInformationContract.Event.ConfirmClicked ->
                confirmClicked()

            is PersonalInformationContract.Event.NameChanged ->
                setState { copy(name = event.name).validate() }

            is PersonalInformationContract.Event.LastNameChanged ->
                setState { copy(lastName = event.lastName).validate() }

            is PersonalInformationContract.Event.CountryChanged ->
                setState { copy(country = event.country).validate() }

            is PersonalInformationContract.Event.DateChanged -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = event.date
                setState { copy(dateOfBirth = calendar).validate() }
            }
        }
    }

    private fun confirmClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            //show loading
            setState { copy(loadingVisible = true) }

            val response = kycApi.completeLevel1Verification(
                firstName = currentState.name,
                lastName = currentState.lastName,
                country = currentState.country!!,
                dateOfBirth = currentState.dateOfBirth?.time!!,
            )

            //dismiss loading
            setState { copy(loadingVisible = false) }

            when (response.status) {
                Status.SUCCESS -> {
                    setEffect { PersonalInformationContract.Effect.Dismiss }
                }

                Status.ERROR ->
                    setEffect {
                        PersonalInformationContract.Effect.ShowToast(
                            response.message ?: "" //todo: Default message
                        )
                    }
            }
        }
    }

    private fun PersonalInformationContract.State.validate() = copy(
        confirmEnabled = dateOfBirth != null
                && country != null
                && textValidator(name)
                && textValidator(lastName)
    )
}