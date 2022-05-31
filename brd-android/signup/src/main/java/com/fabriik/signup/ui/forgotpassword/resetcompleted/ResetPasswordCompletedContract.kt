package com.fabriik.signup.ui.forgotpassword.resetcompleted

import com.fabriik.common.ui.base.FabriikContract

interface ResetPasswordCompletedContract {

    sealed class Event : FabriikContract.Event {
        object LoginClicked: Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoToLogin : Effect()
    }

    class State: FabriikContract.State
}