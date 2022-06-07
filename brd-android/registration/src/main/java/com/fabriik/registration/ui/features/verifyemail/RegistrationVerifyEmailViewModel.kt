package com.fabriik.registration.ui.features.verifyemail

import android.app.Application
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.text.toSpannable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.common.utils.validators.ConfirmationCodeValidator
import com.fabriik.registration.R
import com.fabriik.registration.data.RegistrationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance

class RegistrationVerifyEmailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application, savedStateHandle
), KodeinAware {

    override val kodein by closestKodein { application }
    private val userManager by instance<BrdUserManager>()
    private val registrationApi = RegistrationApi.create()

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

            is RegistrationVerifyEmailContract.Event.ConfirmClicked -> {
                verifyEmail()

                // todo: remove after API integration, added only for testing
                /*if (currentState.code == "111111") {
                    setState { copy(codeErrorVisible = true).validate() }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        setState { copy(verifiedOverlayVisible = true) }
                        delay(1000)
                        setState { copy(verifiedOverlayVisible = false) }
                        setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }
                    }
                }*/
            }
        }
    }

    private fun verifyEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = registrationApi.associateAccountConfirm(currentState.code)

                setState { copy(verifiedOverlayVisible = true) }
                delay(1000)
                setState { copy(verifiedOverlayVisible = false) }
                setEffect { RegistrationVerifyEmailContract.Effect.Dismiss }

            } catch (ex: Exception) {
                Log.i("test_api", ex.message ?: "unknown error")
            }
        }
    }

    private fun resendEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = registrationApi.resendAssociateAccountChallenge()
                Log.i("test_api", "Resend: ${response.string()}")
            } catch (ex: Exception) {
                Log.i("test_api", ex.message ?: "unknown error")
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