package com.fabriik.registration.ui.features.verifyemail

import android.app.Application
import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.core.text.toSpannable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.common.utils.validators.ConfirmationCodeValidator
import com.fabriik.registration.R
import com.fabriik.registration.data.RegistrationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationVerifyEmailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application, savedStateHandle
) {

    private val registrationApi = RegistrationApi.create(application)

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
                setState {
                    copy(
                        code = event.code,
                        codeErrorVisible = false
                    ).validate()
                }

            is RegistrationVerifyEmailContract.Event.ResendEmailClicked ->
                resendEmail()

            is RegistrationVerifyEmailContract.Event.ChangeEmailClicked ->
                setEffect { RegistrationVerifyEmailContract.Effect.Back }

            is RegistrationVerifyEmailContract.Event.ConfirmClicked ->
                verifyEmail()
        }
    }

    private fun verifyEmail() {
        viewModelScope.launch(Dispatchers.IO) {

            // show loading
            setState { copy(loadingVisible = true) }

            val response = registrationApi.associateAccountConfirm(currentState.code)

            // dismiss loading
            setState { copy(loadingVisible = false) }

            when (response.status) {
                Status.SUCCESS ->
                    showCompletedState()

                Status.ERROR ->
                    setState { copy(codeErrorVisible = true) }
            }
        }
    }

    private suspend fun showCompletedState() {
        setState { copy(verifiedOverlayVisible = true) }
        delay(1000)
        setState { copy(verifiedOverlayVisible = false) }
        setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }
    }

    private fun resendEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = registrationApi.resendAssociateAccountChallenge()
            when (response.status) {
                Status.SUCCESS -> {
                    //todo: show success message
                }

                Status.ERROR -> {
                    //empty
                }
            }
        }
    }

    private fun createSubtitle(): CharSequence {
        val email = arguments.email
        val fullText = getString(R.string.Registration_VerifyEmail_Subtitle, email)
        val startIndex = fullText.indexOf(email)
        val spannable = fullText.toSpannable()
        spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, startIndex + email.length, 0)
        return spannable
    }

    private fun RegistrationVerifyEmailContract.State.validate() =
        copy(confirmEnabled = ConfirmationCodeValidator(code) && !codeErrorVisible)
}