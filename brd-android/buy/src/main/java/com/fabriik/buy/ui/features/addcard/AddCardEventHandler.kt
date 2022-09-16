package com.fabriik.buy.ui.features.addcard

import com.fabriik.common.ui.base.FabriikEventHandler

interface AddCardEventHandler: FabriikEventHandler<AddCardContract.Event> {

    override fun handleEvent(event: AddCardContract.Event) {
        return when (event) {
            is AddCardContract.Event.BackClicked -> onBackClicked()
            is AddCardContract.Event.DismissClicked -> onDismissClicked()
            is AddCardContract.Event.ConfirmClicked -> onConfirmClicked()
            is AddCardContract.Event.OnDateChanged -> onExpirationDateChanged(event.date)
            is AddCardContract.Event.OnCardNumberChanged -> onCardNumberChanged(event.number)
            is AddCardContract.Event.OnSecurityCodeChanged -> onSecurityCodeChanged(event.code)
            is AddCardContract.Event.SecurityCodeInfoClicked -> onSecurityCodeInfoClicked()
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onConfirmClicked()

    fun onSecurityCodeInfoClicked()

    fun onCardNumberChanged(cardNumber: String)

    fun onSecurityCodeChanged(securityCode: String)

    fun onExpirationDateChanged(expirationDate: String)
}