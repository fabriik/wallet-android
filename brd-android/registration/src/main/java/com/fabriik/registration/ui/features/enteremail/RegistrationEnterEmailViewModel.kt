package com.fabriik.registration.ui.features.enteremail

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.EmailValidator
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.utils.RegistrationUtils
import com.platform.tools.TokenHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class RegistrationEnterEmailViewModel(
    application: Application
) : FabriikViewModel<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Event, RegistrationEnterEmailContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }
    private val userManager by instance<BrdUserManager>()

    private val registrationApi = RegistrationApi.create()
    private val registrationUtils = RegistrationUtils(userManager)

    override fun createInitialState() = RegistrationEnterEmailContract.State()

    override fun handleEvent(event: RegistrationEnterEmailContract.Event) {
        when (event) {
            is RegistrationEnterEmailContract.Event.EmailChanged ->
                setState { copy(email = event.email).validate() }

            is RegistrationEnterEmailContract.Event.DismissClicked ->
                setEffect { RegistrationEnterEmailContract.Effect.Dismiss }

            is RegistrationEnterEmailContract.Event.NextClicked -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val token = TokenHolder.retrieveToken() ?: return@launch
                        //todo: show error

                        val response = registrationApi.associateAccount(
                            email = currentState.email,
                            token = token,
                            headers = registrationUtils.getAssociateRequestHeaders(
                                email = currentState.email,
                                token = token
                            )
                        )

                        userManager.putSession(
                            response.data.sessionKey
                        )

                        setEffect {
                            RegistrationEnterEmailContract.Effect.GoToVerifyEmail(
                                currentState.email
                            )
                        }
                    } catch (ex: Exception) {
                        Log.i("test_api", ex.message ?: "unknown error")
                    }
                }
            }
        }
    }

    private fun RegistrationEnterEmailContract.State.validate() = copy(
        nextEnabled = EmailValidator(email)
    )
}