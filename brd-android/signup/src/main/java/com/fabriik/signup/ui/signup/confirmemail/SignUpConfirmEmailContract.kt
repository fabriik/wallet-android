package com.fabriik.signup.ui.signup.confirmemail

import com.fabriik.common.ui.base.FabriikContract

interface SignUpConfirmEmailContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked: Event()
        object ResendCodeClicked: Event()
        class ConfirmationCodeChanged(val confirmationCode: String): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoToLogin : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
    }

    data class State(
        val confirmationCode: String = "",
        val confirmationCodeValid: Boolean = false
    ): FabriikContract.State
}