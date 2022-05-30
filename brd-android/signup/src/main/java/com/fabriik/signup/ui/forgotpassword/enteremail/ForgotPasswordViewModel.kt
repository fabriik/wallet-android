package com.fabriik.signup.ui.forgotpassword.enteremail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fabriik.signup.R
import com.fabriik.common.data.Status
import com.fabriik.signup.data.UserApi
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.signup.utils.getString
import com.fabriik.signup.utils.validators.EmailValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    application: Application
) : FabriikViewModel<ForgotPasswordContract.State, ForgotPasswordContract.Event, ForgotPasswordContract.Effect>(application) {

    private val userApi = UserApi.create(application.applicationContext)

    override fun createInitialState() = ForgotPasswordContract.State()

    override fun handleEvent(event: ForgotPasswordContract.Event) {
        when (event) {
            is ForgotPasswordContract.Event.ConfirmClicked ->
                validateForgotPasswordData()

            is ForgotPasswordContract.Event.EmailChanged ->
                setState {
                    copy(
                        email = event.email,
                        emailValid = EmailValidator(event.email)
                    )
                }
        }
    }

    private fun validateForgotPasswordData() {
        val validData = currentState.emailValid

        if (validData) {
            requestPasswordReset(
                email = currentState.email
            )
        } else {
            setEffect {
                ForgotPasswordContract.Effect.ShowSnackBar(
                    getString(R.string.ForgotPassword_EnterValidEmail)
                )
            }
        }
    }

    private fun requestPasswordReset(email: String) {
        // execute API call
        viewModelScope.launch(Dispatchers.IO) {
            setEffect {
                ForgotPasswordContract.Effect.ShowLoading(true)
            }

            val response = userApi.startPasswordReset(email)

            setEffect {
                ForgotPasswordContract.Effect.ShowLoading(false)
            }

            when (response.status) {
                Status.SUCCESS -> {
                    setEffect {
                        ForgotPasswordContract.Effect.GoToResetPassword
                    }
                }
                else -> {
                    setEffect {
                        ForgotPasswordContract.Effect.ShowSnackBar(
                            response.message ?: getString(R.string.SignUp_DefaultErrorMessage)
                        )
                    }
                }
            }
        }
    }
}