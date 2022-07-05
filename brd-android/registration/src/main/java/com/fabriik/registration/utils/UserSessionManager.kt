package com.fabriik.registration.utils

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import com.breadwallet.app.ApplicationLifecycleObserver
import com.breadwallet.tools.security.BrdUserManager
import com.fabriik.common.data.Status
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.ui.RegistrationActivity
import com.fabriik.registration.ui.RegistrationFlow
import com.platform.tools.SessionHolder
import com.platform.tools.SessionState
import com.platform.tools.TokenHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.util.*

object UserSessionManager: KodeinAware {

    override val kodein by closestKodein { context }
    private val registrationApi by instance<RegistrationApi>()
    private val registrationUtils by instance<RegistrationUtils>()
    private val userManager by instance<BrdUserManager>()

    private lateinit var context: Context

    private val sessionExpiredErrorCode = "105"
    private var canShowSessionExpiredScreen = true

    init {
        ApplicationLifecycleObserver.addApplicationLifecycleListener { event ->
            if (event == Lifecycle.Event.ON_START) {
                canShowSessionExpiredScreen = true
            }
        }
    }

    fun provideContext(context: Context) {
        this.context = context
    }

    @Synchronized
    fun checkIfSessionExpired(
        context: Context, scope: CoroutineScope, response: Response
    ) {
        if (SessionHolder.isDefaultSession() || !isSessionExpiredError(response) || !canShowSessionExpiredScreen) {
            return
        }

        userManager.updateVerifyPrompt(true)
        SessionHolder.updateSessionState(SessionState.EXPIRED)
        canShowSessionExpiredScreen = false

        scope.launch(Dispatchers.IO) {
            requestSessionVerification(context)
        }
    }

    suspend fun requestSessionVerification(context: Context) {
        val token = TokenHolder.retrieveToken() ?: return
        val nonce = UUID.randomUUID().toString()
        val responseAssociate = registrationApi.associateNewDevice(
            nonce,
            token,
            registrationUtils.getAssociateRequestHeaders(
                salt = nonce,
                token = token
            )
        )

        SessionHolder.updateSession(
            sessionKey = responseAssociate.data?.sessionKey!!,
            state = SessionState.CREATED
        )

        when (responseAssociate.status) {
            Status.SUCCESS -> {
                val intent = RegistrationActivity.getStartIntent(
                    context = context,
                    args = RegistrationActivity.Args(
                        flow = RegistrationFlow.RE_VERIFY,
                        email = responseAssociate.data?.email!!
                    )
                )
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                context.startActivity(intent)
            }
            Status.ERROR -> {}
        }
    }

    private fun isSessionExpiredError(response: Response): Boolean {
        if (response.isSuccessful) {
            return false
        }

        return try {
            val responseJson = response.peekBody(Long.MAX_VALUE).string()
            val jsonObject = JSONObject(responseJson)
            val errorObject = jsonObject.getJSONObject("error")
            val errorCode = errorObject.getString("code")
            errorCode == sessionExpiredErrorCode
        } catch (ex: Exception) {
            false
        }
    }
}