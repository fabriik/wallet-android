package com.fabriik.signup.ui.signup.info

import android.app.Application
import androidx.lifecycle.*
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Status
import com.fabriik.signup.R
import com.fabriik.signup.data.UserApi
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.signup.utils.getString
import com.fabriik.signup.utils.validators.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpInfoViewModel(
    application: Application
) : FabriikViewModel<SignUpInfoContract.State, SignUpInfoContract.Event, SignUpInfoContract.Effect>(
    application
) {

    private val userApi = UserApi.create(application.applicationContext)

    override fun createInitialState() = SignUpInfoContract.State()

    override fun handleEvent(event: SignUpInfoContract.Event) {
        when (event) {
            is SignUpInfoContract.Event.EmailChanged ->
                setState {
                    copy(
                        email = event.email,
                        emailValid = EmailValidator(event.email)
                    )
                }

            is SignUpInfoContract.Event.PhoneChanged ->
                setState {
                    copy(
                        phone = event.phone,
                        phoneValid = PhoneNumberValidator(event.phone)
                    )
                }

            is SignUpInfoContract.Event.PasswordChanged ->
                setState {
                    copy(
                        password = event.password,
                        passwordValid = PasswordValidator(event.password)
                    )
                }

            is SignUpInfoContract.Event.LastNameChanged ->
                setState {
                    copy(
                        lastName = event.lastName,
                        lastNameValid = TextValidator(event.lastName)
                    )
                }

            is SignUpInfoContract.Event.FirstNameChanged ->
                setState {
                    copy(
                        firstName = event.firstName,
                        firstNameValid = TextValidator(event.firstName)
                    )
                }

            is SignUpInfoContract.Event.TermsChanged ->
                setState {
                    copy(
                        termsAccepted = event.checked
                    )
                }

            is SignUpInfoContract.Event.PrivacyPolicyClicked ->
                setEffect {
                    SignUpInfoContract.Effect.OpenWebsite(
                        FabriikApiConstants.URL_PRIVACY_POLICY
                    )
                }

            is SignUpInfoContract.Event.UserAgreementClicked ->
                setEffect {
                    SignUpInfoContract.Effect.OpenWebsite(
                        FabriikApiConstants.URL_TERMS_AND_CONDITIONS
                    )
                }

            is SignUpInfoContract.Event.SubmitClicked ->
                validateRegistrationData()
        }
    }

    private fun validateRegistrationData() {
        val validData = currentState.emailValid && currentState.phoneValid
                && currentState.passwordValid && currentState.lastNameValid
                && currentState.firstNameValid && currentState.termsAccepted

        if (validData) {
            register(
                email = currentState.email,
                phone = currentState.phone,
                password = currentState.password,
                lastName = currentState.lastName,
                firstName = currentState.firstName
            )
        } else {
            setEffect {
                SignUpInfoContract.Effect.ShowSnackBar(
                    getString(R.string.SignUp_EnterValidData)
                )
            }
        }
    }

    private fun register(
        email: String, password: String, firstName: String, lastName: String, phone: String
    ) {
        // execute API call
        viewModelScope.launch(Dispatchers.IO) {
            setEffect {
                SignUpInfoContract.Effect.ShowLoading(true)
            }

            val response = userApi.register(
                email = email,
                phone = phone,
                password = password,
                lastName = lastName,
                firstName = firstName
            )

            setEffect {
                SignUpInfoContract.Effect.ShowLoading(false)
            }

            when {
                response.status == Status.SUCCESS && response.data != null -> {
                    setEffect {
                        SignUpInfoContract.Effect.GoToConfirmation(
                            response.data!!.sessionKey
                        )
                    }
                }
                else -> {
                    setEffect {
                        SignUpInfoContract.Effect.ShowSnackBar(
                            response.message ?: getString(R.string.SignUp_DefaultErrorMessage)
                        )
                    }
                }
            }
        }
    }
}