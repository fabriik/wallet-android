package com.fabriik.signup.ui.kyc.personalinfo

import com.fabriik.common.ui.base.FabriikContract
import java.util.*

interface KycPersonalInfoContract {

    sealed class Event : FabriikContract.Event {
        object NextClicked: Event()
        object DateOfBirthClicked: Event()
        class TaxIdChanged(val taxId: String): Event()
        class DateOfBirthChanged(val date: Date?): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoToIdUpload : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
        class OpenDatePicker(val date: Date?) : Effect()
    }

    data class State(
        val dateOfBirth: Date? = null,
        val dateOfBirthValid: Boolean = false,
        val taxId: String = "",
        val taxIdValid: Boolean = false
    ): FabriikContract.State
}