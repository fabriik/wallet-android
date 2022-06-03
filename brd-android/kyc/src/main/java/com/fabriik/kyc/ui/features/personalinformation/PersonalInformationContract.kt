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
        class DayChanged(val day: Int?) : PersonalInformationContract.Event()
        class YearChanged(val year: Int?) : PersonalInformationContract.Event()
        class MonthChanged(val month: Int?) : PersonalInformationContract.Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
    }

    data class State(
        val name: String = "",
        val lastName: String = "",
        val country: Country? = null,
        val day: Int? = null,
        val year: Int? = null,
        val month: Int? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}