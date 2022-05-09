package com.fabriik.kyc.ui.features.exposedperson

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.EmailValidator

class ExposedPersonViewModel(
    application: Application
) : FabriikViewModel<ExposedPersonContract.State, ExposedPersonContract.Event, ExposedPersonContract.Effect>(application) {

    private val emailValidator = EmailValidator

    override fun createInitialState() = ExposedPersonContract.State()

    override fun handleEvent(event: ExposedPersonContract.Event) {
        when (event) {
            is ExposedPersonContract.Event.ConfirmClicked ->
                setEffect {
                    ExposedPersonContract.Effect.Dismiss
                }

            is ExposedPersonContract.Event.EmailChanged ->
                setState { copy(email = event.email).validate() }
        }
    }

    private fun ExposedPersonContract.State.validate() = copy(
        confirmEnabled = emailValidator(email)
    )
}