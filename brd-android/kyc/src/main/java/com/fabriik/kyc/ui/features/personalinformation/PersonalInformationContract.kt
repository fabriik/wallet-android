package com.fabriik.kyc.ui.features.personalinformation

import android.app.Activity
import com.fabriik.common.data.model.Profile
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country
import java.util.*

interface PersonalInformationContract {

    sealed class Event : FabriikContract.Event {
        object LoadProfile : Event()
        object BackClicked : Event()
        object DateClicked : Event()
        object CountryClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()

        class NameChanged(val name: String) : PersonalInformationContract.Event()
        class CountryChanged(val country: Country) : PersonalInformationContract.Event()
        class LastNameChanged(val lastName: String) : PersonalInformationContract.Event()
        class DateChanged(val date: Long) : PersonalInformationContract.Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object CountrySelection : Effect()
        data class Dismiss(val resultCode: Int = Activity.RESULT_CANCELED) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class DateSelection(val date: Calendar?) : Effect()
    }

    data class State(
        val name: String = "",
        val lastName: String = "",
        val country: Country? = null,
        val dateOfBirth: Calendar? = null,
        val confirmEnabled: Boolean = false,
        val loadingVisible: Boolean = false,
        val completedViewVisible: Boolean = false
    ) : FabriikContract.State
}