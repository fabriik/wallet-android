package com.fabriik.kyc.ui.features.personalinformation

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.ProfileManager
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.validators.TextValidator
import com.fabriik.kyc.R
import com.fabriik.kyc.data.KycApi
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.ui.KycActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.time.ZoneOffset.UTC
import java.util.*

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }
    private val profileManager by kodein.instance<ProfileManager>()

    companion object {
        const val MIN_AGE = 18
    }

    private val kycApi = KycApi.create(application.applicationContext)
    private val textValidator = TextValidator

    override fun createInitialState() = PersonalInformationContract.State()

    override fun handleEvent(event: PersonalInformationContract.Event) {
        when (event) {
            is PersonalInformationContract.Event.LoadProfile -> {
                val profile = profileManager.getProfile()
                setState {
                    copy(
                        name = profile?.firstName ?: "",
                        lastName = profile?.lastName ?: "",
                        dateOfBirth = profile?.dateOfBirth,
                        country = profile?.country?.let { Country(it, it) },
                    )
                }
            }

            is PersonalInformationContract.Event.BackClicked ->
                setEffect { PersonalInformationContract.Effect.GoBack }

            is PersonalInformationContract.Event.DismissClicked ->
                setEffect { PersonalInformationContract.Effect.Dismiss() }

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
                val calendar = Calendar.getInstance(SimpleTimeZone(0, "UTC"))
                calendar.timeInMillis = event.date
                setState { copy(dateOfBirth = calendar).validate() }
            }
        }
    }

    private fun showCompletedState() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(completedViewVisible = true) }
            delay(1000)
            setState { copy(completedViewVisible = false) }
            setEffect { PersonalInformationContract.Effect.Dismiss(KycActivity.RESULT_DATA_UPDATED) }
        }
    }

    private fun confirmClicked() {
        if (!isAgeValid()) {
            setEffect {
                PersonalInformationContract.Effect.ShowToast(getString(R.string.KYC_Error_Underage))
            }
            return
        }

        callApi(
            endState = { copy(loadingVisible = false) },
            startState = { copy(loadingVisible = true) },
            action = {
                kycApi.completeLevel1Verification(
                    firstName = currentState.name,
                    lastName = currentState.lastName,
                    country = currentState.country!!,
                    dateOfBirth = currentState.dateOfBirth?.time!!,
                )
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        showCompletedState()
                    }

                    Status.ERROR -> {
                        setEffect {
                            PersonalInformationContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                    }
                }
            }
        )
    }

    private fun PersonalInformationContract.State.validate() = copy(
        confirmEnabled = dateOfBirth != null
                && country != null
                && textValidator(name)
                && textValidator(lastName)
    )

    private fun isAgeValid(): Boolean {
        val currentDate = Calendar.getInstance()
        val targetDate = Calendar.getInstance()
        targetDate.time = currentState.dateOfBirth?.time ?: return false
        targetDate.add(Calendar.YEAR, MIN_AGE)

        return targetDate.before(currentDate)
    }
}