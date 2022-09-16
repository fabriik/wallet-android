package com.fabriik.registration.ui.features.enteremail

import com.fabriik.common.ui.base.FabriikContract

interface RegistrationEnterEmailContract {

    sealed class Event : FabriikContract.Event {
        object NextClicked : Event()
        object DismissClicked : Event()
        data class PromotionsClicked(val checked: Boolean) : Event()
        data class EmailChanged(val email: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String) : Effect()
        data class GoToVerifyEmail(val email: String) : Effect()
    }

    data class State(
        val email: String = "",
        val nextEnabled: Boolean = false,
        val promotionsEnabled: Boolean = true,
        val loadingVisible: Boolean = false
    ): FabriikContract.State
}