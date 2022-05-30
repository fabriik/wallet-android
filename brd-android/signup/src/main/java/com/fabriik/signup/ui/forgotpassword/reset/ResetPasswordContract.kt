package com.fabriik.signup.ui.forgotpassword.reset

import com.fabriik.common.ui.base.FabriikContract

interface ResetPasswordContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked: Event()
        class NewPasswordChanged(val password: String): Event()
        class ConfirmPasswordChanged(val password: String): Event()
        class ConfirmationCodeChanged(val code: String): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoToResetCompleted : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
    }

    data class State(
        val code: String = "",
        val codeValid: Boolean = false,
        val password: String = "",
        val passwordValid: Boolean = false,
        val passwordConfirm: String = "",
        val passwordConfirmValid: Boolean = false,
    ): FabriikContract.State
}