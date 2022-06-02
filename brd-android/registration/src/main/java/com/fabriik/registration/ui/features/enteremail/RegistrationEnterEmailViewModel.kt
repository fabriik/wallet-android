package com.fabriik.registration.ui.features.enteremail

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.breadwallet.crypto.Key
import com.breadwallet.tools.crypto.CryptoHelper
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.validators.EmailValidator
import com.fabriik.registration.data.RegistrationApi
import com.platform.tools.TokenHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.text.SimpleDateFormat
import java.util.*

class RegistrationEnterEmailViewModel(
    application: Application
) : FabriikViewModel<RegistrationEnterEmailContract.State, RegistrationEnterEmailContract.Event, RegistrationEnterEmailContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }
    private val userManager by instance<BrdUserManager>()

    private val registrationApi = RegistrationApi.create()

    override fun createInitialState() = RegistrationEnterEmailContract.State()

    override fun handleEvent(event: RegistrationEnterEmailContract.Event) {
        when (event) {
            is RegistrationEnterEmailContract.Event.EmailChanged ->
                setState { copy(email = event.email).validate() }

            is RegistrationEnterEmailContract.Event.DismissClicked ->
                setEffect { RegistrationEnterEmailContract.Effect.Dismiss }

            is RegistrationEnterEmailContract.Event.NextClicked -> {

                val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
                dateFormat.timeZone = TimeZone.getTimeZone("GMT")

                val key = authKey?: return

                val email = currentState.email
                val token = TokenHolder.retrieveToken() ?: return
                val signatureSha256 = CryptoHelper.sha256((token+email).toByteArray()) ?: return
                val signature = CryptoHelper.signBasicDer(signatureSha256, key)
                val signatureEncoded = Base64.encode(signature, Base64.NO_WRAP)
                val dateHeader = dateFormat.format(Date())

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val response = registrationApi.associateAccount(
                            email = email,
                            token = token,
                            dateHeader = dateHeader,
                            signature = String(signatureEncoded).trim()
                        )

                        Log.i("test_api", response.string())
                    } catch (ex: Exception) {
                        Log.i("test_api", ex.message ?: "unknown error")
                    }
                }
            }
                /*setEffect {
                    RegistrationEnterEmailContract.Effect.GoToVerifyEmail(currentState.email)
                }*/
        }
    }

    private var authKey: Key? = null
        get() {
            if (field == null) {
                val key = userManager.getAuthKey() ?: byteArrayOf()
                if (key.isNotEmpty()) {
                    field = Key.createFromPrivateKeyString(key).orNull()
                }
            }
            return field
        }

    private fun RegistrationEnterEmailContract.State.validate() = copy(
        nextEnabled = EmailValidator(email)
    )
}