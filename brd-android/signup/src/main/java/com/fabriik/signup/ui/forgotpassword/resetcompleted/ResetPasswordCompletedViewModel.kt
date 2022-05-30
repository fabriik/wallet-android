package com.fabriik.signup.ui.forgotpassword.resetcompleted

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class ResetPasswordCompletedViewModel(
    application: Application
) : FabriikViewModel<ResetPasswordCompletedContract.State, ResetPasswordCompletedContract.Event, ResetPasswordCompletedContract.Effect>(application) {

    override fun createInitialState() = ResetPasswordCompletedContract.State()

    override fun handleEvent(event: ResetPasswordCompletedContract.Event) {
        when (event) {
            is ResetPasswordCompletedContract.Event.LoginClicked -> {
                setEffect {
                    ResetPasswordCompletedContract.Effect.GoToLogin
                }
            }
        }
    }
}