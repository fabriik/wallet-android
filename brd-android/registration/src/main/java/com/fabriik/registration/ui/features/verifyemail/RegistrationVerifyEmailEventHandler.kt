package com.fabriik.registration.ui.features.verifyemail

import com.fabriik.common.ui.base.FabriikEventHandler

interface RegistrationVerifyEmailEventHandler: FabriikEventHandler<RegistrationVerifyEmailContract.Event> {

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        return when (event) {
            is RegistrationVerifyEmailContract.Event.ConfirmClicked -> onConfirmClicked()
            is RegistrationVerifyEmailContract.Event.DismissClicked -> onDismissClicked()
            is RegistrationVerifyEmailContract.Event.ChangeEmailClicked -> onChangeEmailClicked()
            is RegistrationVerifyEmailContract.Event.ResendEmailClicked -> onResendEmailClicked()
            is RegistrationVerifyEmailContract.Event.CodeChanged -> onCodeChanged(event.code)
        }
    }

    fun onConfirmClicked()

    fun onDismissClicked()

    fun onChangeEmailClicked()

    fun onResendEmailClicked()

    fun onCodeChanged(code: String)
}