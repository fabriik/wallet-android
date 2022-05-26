package com.fabriik.kyc.ui.features.personalinformation

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

interface PersonalInformationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()

        class NameChanged(val name: String) : PersonalInformationContract.Event()
        class CountryChanged(val country: Country) : PersonalInformationContract.Event()
        class LastNameChanged(val lastName: String) : PersonalInformationContract.Event()
        class ExposedPersonChanged(val exposedPerson: Boolean) : PersonalInformationContract.Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToExposedPerson : Effect()
        object GoToAccountVerification : Effect()
    }

    data class State(
        val name: String = "",
        val lastName: String = "",
        val country: Country? = null,
        val exposedPerson: Boolean? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}