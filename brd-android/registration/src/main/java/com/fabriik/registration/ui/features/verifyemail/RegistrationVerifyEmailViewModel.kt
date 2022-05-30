package com.fabriik.registration.ui.features.verifyemail

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.registration.R

class RegistrationVerifyEmailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Event, RegistrationVerifyEmailContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: RegistrationVerifyEmailFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = RegistrationVerifyEmailFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = RegistrationVerifyEmailContract.State(
        subtitle = getString(R.string.Registration_VerifyEmail_Subtitle, arguments.email)
    )

    override fun handleEvent(event: RegistrationVerifyEmailContract.Event) {
        when (event) {

        }
    }
}