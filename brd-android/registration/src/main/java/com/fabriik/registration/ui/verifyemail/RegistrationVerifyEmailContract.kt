package com.fabriik.registration.ui.verifyemail

import com.fabriik.common.ui.base.FabriikContract

interface RegistrationVerifyEmailContract {

    sealed class Event : FabriikContract.Event {

    }

    sealed class Effect : FabriikContract.Effect {

    }

    data class State(
        val confirmEnabled: Boolean = false
    ): FabriikContract.State
}