package com.fabriik.signup.ui.login

import android.app.Application
import androidx.lifecycle.*
import com.fabriik.signup.R
import com.fabriik.common.data.Status
import com.fabriik.signup.data.UserApi
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.signup.utils.getString
import com.fabriik.signup.utils.validators.EmailValidator
import com.fabriik.signup.utils.validators.PasswordValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInViewModel(
    application: Application
) : FabriikViewModel<LogInContract.State, LogInContract.Event, LogInContract.Effect>(application) {

    private val userApi = UserApi.create(application.applicationContext)

    override fun createInitialState() = LogInContract.State()

    override fun handleEvent(event: LogInContract.Event) {
        when (event) {
            is LogInContract.Event.EmailChanged ->
                setState {
                    copy(
                        email = event.email,
                        emailValid = EmailValidator(event.email)
                    )
                }

            is LogInContract.Event.PasswordChanged ->
                setState {
                    copy(
                        password = event.password,
                        passwordValid = PasswordValidator(event.password)
                    )
                }

            is LogInContract.Event.SubmitClicked ->
                validateLoginData()

            is LogInContract.Event.SignUpClicked ->
                setEffect {
                    LogInContract.Effect.GoToSignUp
                }

            is LogInContract.Event.ForgotPasswordClicked ->
                setEffect {
                    LogInContract.Effect.GoToForgotPassword
                }
        }
    }

    private fun validateLoginData() {
        val validData = currentState.emailValid && currentState.passwordValid

        if (validData) {
            login(
                email = currentState.email,
                password = currentState.password
            )
        } else {
            setEffect {
                LogInContract.Effect.ShowSnackBar(
                    getString(R.string.LogIn_EnterValidData)
                )
            }
        }
    }

    private fun login(email: String, password: String) {
        // execute API call
        viewModelScope.launch(Dispatchers.IO) {
            setEffect {
                LogInContract.Effect.ShowLoading(true)
            }

            val response = userApi.login(
                username = email,
                password = password
            )

            setEffect {
                LogInContract.Effect.ShowLoading(false)
            }

            when (response.status) {
                Status.SUCCESS -> {
                    setEffect {
                        LogInContract.Effect.ShowSnackBar( // todo: replace with "LogInContract.Effect.GoToKyc" when KYC is ready
                            getString(R.string.LogIn_Completed)
                        )
                    }
                }
                else -> {
                    setEffect {
                        LogInContract.Effect.ShowSnackBar(
                            response.message ?: getString(R.string.SignUp_DefaultErrorMessage)
                        )
                    }
                }
            }
        }
    }
}