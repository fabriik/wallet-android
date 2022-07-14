package com.fabriik.trade.ui.features.authentication

import com.fabriik.common.ui.base.FabriikContract

interface SwapAuthenticationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object AuthSucceeded : Event()
        data class AuthFailed(val errorCode: Int) : Event()
        data class PinValidated(val valid: Boolean) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object ShakeError : Effect()
        data class Back(val resultKey: String) : Effect()
    }

    data class State(
        val isFingerprintEnabled: Boolean,
        val authMode: AuthMode
    ) : FabriikContract.State

    enum class AuthMode {
        /** Attempt biometric auth if configured, otherwise the pin is required. */
        USER_PREFERRED,

        /** Ensures the use of a pin, fails immediately if not set. */
        PIN_REQUIRED,

        /** Ensures the use of biometric auth, fails immediately if not available. */
        BIOMETRIC_REQUIRED
    }
}