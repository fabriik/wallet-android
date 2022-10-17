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
import java.util.*

class PersonalInformationViewModel(
    application: Application
) : FabriikViewModel<PersonalInformationContract.State, PersonalInformationContract.Event, PersonalInformationContract.Effect>(
    application
), PersonalInformationEventHandler, KodeinAware {

    override val kodein by closestKodein { application }
    private val profileManager by kodein.instance<ProfileManager>()

    companion object {
        const val MIN_AGE = 18
    }

    private val kycApi = KycApi.create(application.applicationContext)
    private val textValidator = TextValidator

    override fun createInitialState() = PersonalInformationContract.State()

    override fun onBackClicked() {
        setEffect { PersonalInformationContract.Effect.GoBack }
    }

    override fun onDismissClicked() {
        setEffect { PersonalInformationContract.Effect.Dismiss() }
    }

    override fun onCountryClicked() {
        setEffect { PersonalInformationContract.Effect.CountrySelection }
    }

    override fun onNameChanged(name: String) {
        setState { copy(name = name).validate() }
    }

    override fun onLastNameChanged(lastName: String) {
        setState { copy(lastName = lastName).validate() }
    }

    override fun onCountryChanged(country: Country) {
        setState { copy(country = country).validate() }
    }

    override fun onDateClicked() {
        setEffect { PersonalInformationContract.Effect.DateSelection(currentState.dateOfBirth) }
    }

    override fun onDateChanged(date: Long) {
        val calendar = Calendar.getInstance(SimpleTimeZone(0, "UTC"))
        calendar.timeInMillis = date
        setState { copy(dateOfBirth = calendar).validate() }
    }

    override fun onLoadProfile() {
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

    private fun showCompletedState() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(completedViewVisible = true) }
            delay(1000)
            setState { copy(completedViewVisible = false) }
            setEffect { PersonalInformationContract.Effect.Dismiss(KycActivity.RESULT_DATA_UPDATED) }
        }
    }

    override fun onConfirmClicked() {
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
                                it.message ?: getString(R.string.Api_DefaultError)
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