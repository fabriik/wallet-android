package com.fabriik.registration.ui.features.verifyemail

import com.fabriik.common.ui.base.FabriikContract

interface RegistrationVerifyEmailContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object ResendEmailClicked : Event()
        object ChangeEmailClicked : Event()
        data class CodeChanged(val code: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back: Effect()
        object Dismiss: Effect()
        data class ShowToast(val message: String) : Effect()
    }

    data class State(
        val code: String = "",
        val subtitle: CharSequence,
        val confirmEnabled: Boolean = false,
        val loadingVisible: Boolean = false,
        val codeErrorVisible: Boolean = false,
        val verifiedOverlayVisible: Boolean = false
    ): FabriikContract.State
}