package com.fabriik.registration.ui.enteremail

import com.fabriik.common.ui.base.FabriikContract

interface RegistrationEnterEmailContract {

    sealed class Event : FabriikContract.Event {

    }

    sealed class Effect : FabriikContract.Effect {

    }

    data class State(
        val nextEnabled: Boolean = false
    ): FabriikContract.State
}