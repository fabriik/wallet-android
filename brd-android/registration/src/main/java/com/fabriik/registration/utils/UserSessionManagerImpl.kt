package com.fabriik.registration.utils

import android.content.Context
import android.content.Intent
import com.fabriik.common.data.FabriikApiResponseError
import com.fabriik.common.data.Status
import com.fabriik.common.utils.UserSessionExpiredException
import com.fabriik.registration.data.RegistrationApi
import com.fabriik.registration.ui.RegistrationActivity
import com.fabriik.registration.ui.RegistrationFlow
import com.platform.tools.TokenHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*

class UserSessionManagerImpl(
    private val registrationApi: RegistrationApi,
    private val registrationUtils: RegistrationUtils
) : UserSessionManager {

    private val sessionExpiredErrorCode = "105"

    override fun onSessionExpired(context: Context, scope: CoroutineScope) {
        val token = TokenHolder.retrieveToken() ?: return
        val nonce = UUID.randomUUID().toString()

        scope.launch(Dispatchers.IO) {
            val response = registrationApi.associateNewDevice(
                nonce,
                token,
                registrationUtils.getAssociateRequestHeaders(
                    salt = nonce,
                    token = token
                )
            )

            when (response.status) {
                Status.SUCCESS -> {
                    val intent = RegistrationActivity.getStartIntent(
                        context = context,
                        args = RegistrationActivity.Args(
                            flow = RegistrationFlow.RE_VERIFY,
                            email = response.data?.email!!
                        )
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                Status.ERROR -> {}
            }
        }
    }

    override fun isSessionExpiredError(error: Throwable?) : Boolean {
        return (error is UserSessionExpiredException)
    }
}