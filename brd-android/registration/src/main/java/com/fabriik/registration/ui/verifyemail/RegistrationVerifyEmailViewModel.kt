package com.fabriik.registration.ui.verifyemail

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class RegistrationVerifyEmailViewModel(
    application: Application
) : FabriikViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application
) {

    override fun createInitialState() = RegistrationVerifyEmailContract.State()

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        when (event) {

        }
    }
}