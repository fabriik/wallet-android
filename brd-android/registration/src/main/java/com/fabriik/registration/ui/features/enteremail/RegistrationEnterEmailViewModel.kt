package com.fabriik.registration.ui.features.enteremail

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.EmailValidator

class RegistrationEnterEmailViewModel(
    application: Application
) : FabriikViewModel<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Event, RegistrationEnterEmailContract.Effect>(
    application
) {

    override fun createInitialState() = RegistrationEnterEmailContract.State()

    override fun handleEvent(event: RegistrationEnterEmailContract.Event) {
        when (event) {
            is RegistrationEnterEmailContract.Event.EmailChanged ->
                setState { copy(email = event.email).validate() }

            is RegistrationEnterEmailContract.Event.DismissClicked ->
                setEffect { RegistrationEnterEmailContract.Effect.Dismiss }

            is RegistrationEnterEmailContract.Event.NextClicked ->
                setEffect {
                    RegistrationEnterEmailContract.Effect.GoToVerifyEmail(currentState.email)
                }
        }
    }

    private fun RegistrationEnterEmailContract.State.validate() = copy(
        nextEnabled = EmailValidator(email)
    )
}