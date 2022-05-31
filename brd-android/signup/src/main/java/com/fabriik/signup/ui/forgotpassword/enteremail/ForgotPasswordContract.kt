package com.fabriik.signup.ui.forgotpassword.enteremail

import com.fabriik.common.ui.base.FabriikContract

interface ForgotPasswordContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked: Event()
        class EmailChanged(val email: String): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoToResetPassword : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
    }

    data class State(
        val email: String = "",
        val emailValid: Boolean = false
    ): FabriikContract.State
}