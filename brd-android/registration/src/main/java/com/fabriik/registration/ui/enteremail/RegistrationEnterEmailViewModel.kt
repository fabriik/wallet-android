package com.fabriik.registration.ui.enteremail

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class RegistrationEnterEmailViewModel(
    application: Application
) : FabriikViewModel<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Event, RegistrationEnterEmailContract.Effect>(
    application
) {

    override fun createInitialState() = RegistrationEnterEmailContract.State()

    override fun handleEvent(event: RegistrationEnterEmailContract.Event) {
        when (event) {

        }
    }
}