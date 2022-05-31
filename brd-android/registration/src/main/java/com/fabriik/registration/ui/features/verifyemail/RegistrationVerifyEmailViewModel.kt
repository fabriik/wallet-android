package com.fabriik.registration.ui.features.verifyemail

import android.app.Application
import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.core.text.toSpannable
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
        subtitle = createSubtitle()
    )

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        when (event) {
            is RegistrationVerifyEmailContract.Event.DismissClicked ->
                setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }

            is RegistrationVerifyEmailContract.Event.CodeChanged ->
                setState { copy(code = event.code).validate() }

            is RegistrationVerifyEmailContract.Event.ResendEmailClicked ->
                resendEmail()

            is RegistrationVerifyEmailContract.Event.ChangeEmailClicked ->
                setEffect { RegistrationVerifyEmailContract.Effect.Back }

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

    private fun resendEmail() {
        // todo: call API
    }

    private fun createSubtitle(): CharSequence {
        val email = arguments.email
        val fullText = getString(R.string.Registration_VerifyEmail_Subtitle, email)
        val startIndex = fullText.indexOf(email)
        val spannable = fullText.toSpannable()
        spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + email.length,0)
        return spannable
    }

    private fun RegistrationVerifyEmailContract.State.validate() =
        copy(confirmEnabled = ConfirmationCodeValidator(code))
}