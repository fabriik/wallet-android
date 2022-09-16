package com.fabriik.registration.ui.features.enteremail

import com.fabriik.common.ui.base.FabriikEventHandler

interface RegistrationEnterEmailEventHandler: FabriikEventHandler<RegistrationEnterEmailContract.Event> {

    override fun handleEvent(event: RegistrationEnterEmailContract.Event) {
        return when (event) {
            is RegistrationEnterEmailContract.Event.NextClicked -> onNextClicked()
            is RegistrationEnterEmailContract.Event.DismissClicked -> onDismissClicked()
            is RegistrationEnterEmailContract.Event.EmailChanged -> onEmailChanged(event.email)
            is RegistrationEnterEmailContract.Event.PromotionsClicked -> onPromotionsClicked(event.checked)
        }
    }

    fun onNextClicked()

    fun onDismissClicked()

    fun onEmailChanged(email: String)

    fun onPromotionsClicked(isChecked: Boolean)
}