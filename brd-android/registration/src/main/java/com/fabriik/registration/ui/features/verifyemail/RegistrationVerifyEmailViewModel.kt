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
import com.fabriik.registration.ui.RegistrationFlow
import com.fabriik.registration.ui.RegistrationActivity
import com.platform.tools.SessionHolder
import com.platform.tools.SessionState
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
        subtitle = createSubtitle(),
        changeEmailButtonVisible = arguments.flow != RegistrationFlow.RE_VERIFY
    )

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        when (event) {
            is RegistrationVerifyEmailContract.Event.DismissClicked ->
                setEffect { RegistrationVerifyEmailContract.Effect.Dismiss() }

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
        val currentSession = SessionHolder.getSession()
        if (currentSession.state == SessionState.DEFAULT) {
            setEffect {
                RegistrationVerifyEmailContract.Effect.ShowToast(
                    getString(R.string.FabriikApi_DefaultError)
                )
            }
            return
        }

        callApi(
            endState = { copy(loadingVisible = false) },
            startState = { copy(loadingVisible = true) },
            action = { registrationApi.associateAccountConfirm(currentState.code) },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        SessionHolder.updateSession(
                            sessionKey = currentSession.key,
                            state = SessionState.VERIFIED
                        )

                        showCompletedState()
                    }

                    Status.ERROR ->
                        setState { copy(codeErrorVisible = true) }
                }
            }
        )
    }

    private fun showCompletedState() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(completedViewVisible = true) }
            delay(1000)
            setState { copy(completedViewVisible = false) }
            setEffect {
                RegistrationVerifyEmailContract.Effect.Dismiss(
                    RegistrationActivity.RESULT_VERIFIED
                )
            }
        }
    }

    private fun resendEmail() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { registrationApi.resendAssociateAccountChallenge() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setEffect {
                            RegistrationVerifyEmailContract.Effect.ShowToast(
                                getString(R.string.Registration_VerifyEmail_CodeSent)
                            )
                        }

                    Status.ERROR -> {
                        //empty
                    }
                }
            }
        )
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