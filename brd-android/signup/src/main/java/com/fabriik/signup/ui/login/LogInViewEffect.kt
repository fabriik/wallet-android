package com.fabriik.signup.ui.login

import com.fabriik.signup.ui.base.FabriikViewEffect

sealed class LogInViewEffect : FabriikViewEffect {
    object GoToSignUp: LogInViewEffect()
    object GoToForgotPassword : LogInViewEffect()
    class ShowLoading(val show: Boolean) : LogInViewEffect()
    class ShowSnackBar(val message: String) : LogInViewEffect()
}