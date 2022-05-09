package com.fabriik.kyc.ui.features.personalinformation

import com.fabriik.common.ui.base.FabriikContract

interface PersonalInformationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object ScreenInfoClicked : Event()
        object ExposedPersonInfoClicked : Event()

        class NameChanged(val name: String) : PersonalInformationContract.Event()
        class CountryChanged(val country: String) : PersonalInformationContract.Event()
        class LastNameChanged(val lastName: String) : PersonalInformationContract.Event()
        class ExposedPersonChanged(val exposedPerson: Boolean) : PersonalInformationContract.Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToExposedPerson : Effect()
    }

    data class State(
        val name: String = "",
        val lastName: String = "",
        val country: String = "",
        val exposedPerson: Boolean? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}