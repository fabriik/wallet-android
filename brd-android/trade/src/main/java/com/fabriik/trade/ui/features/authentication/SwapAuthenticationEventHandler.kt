package com.fabriik.trade.ui.features.authentication

import com.fabriik.common.ui.base.FabriikEventHandler

interface SwapAuthenticationEventHandler: FabriikEventHandler<SwapAuthenticationContract.Event> {

    override fun handleEvent(event: SwapAuthenticationContract.Event) {
        return when (event) {
            is SwapAuthenticationContract.Event.BackClicked -> onBackClicked()
            is SwapAuthenticationContract.Event.DismissClicked -> onDismissClicked()
            is SwapAuthenticationContract.Event.AuthSucceeded -> onAuthSucceeded()
            is SwapAuthenticationContract.Event.AuthFailed -> onAuthFailed(event.errorCode)
            is SwapAuthenticationContract.Event.PinValidated -> onPinValidated(event.valid)
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onAuthSucceeded()

    fun onAuthFailed(errorCode: Int)

    fun onPinValidated(valid: Boolean)
}