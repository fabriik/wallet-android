package com.fabriik.registration.ui.features.verifyemail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.common.utils.validators.ConfirmationCodeValidator
import com.fabriik.registration.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationVerifyEmailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: RegistrationVerifyEmailFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = RegistrationVerifyEmailFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = RegistrationVerifyEmailContract.State(
        subtitle = getString(R.string.Registration_VerifyEmail_Subtitle, arguments.email)
    )

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        when (event) {
            is RegistrationVerifyEmailContract.Event.DismissClicked ->
                setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }

            is RegistrationVerifyEmailContract.Event.CodeChanged ->
                setState { copy(code = event.code).validate() }

            is RegistrationVerifyEmailContract.Event.ResendEmailClicked -> {} // todo

            is RegistrationVerifyEmailContract.Event.ChangeEmailClicked -> {} // todo

            is RegistrationVerifyEmailContract.Event.ConfirmClicked -> {
                //todo: API call
                viewModelScope.launch(Dispatchers.IO) {
                    setState { copy(verifiedOverlayVisible = true) }
                    delay(1000)
                    setState { copy(verifiedOverlayVisible = false) }
                    setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }
                }
            }
        }
    }

    private fun RegistrationVerifyEmailContract.State.validate() =
        copy(confirmEnabled = ConfirmationCodeValidator(code))
}