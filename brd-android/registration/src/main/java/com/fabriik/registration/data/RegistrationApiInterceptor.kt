package com.fabriik.registration.data

import android.content.Context
import com.breadwallet.ext.addUniqueHeader
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.registration.utils.UserSessionManager
import com.platform.tools.SessionHolder
import kotlinx.coroutines.CoroutineScope
import okhttp3.Interceptor
import okhttp3.Response

class RegistrationApiInterceptor(
    private val context: Context,
    private val scope: CoroutineScope
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilderWithDeviceId = request
            .newBuilder()
            .addUniqueHeader(request, FabriikApiConstants.HEADER_DEVICE_ID, BRSharedPrefs.getDeviceId())
            .addUniqueHeader(request ,FabriikApiConstants.HEADER_USER_AGENT, FabriikApiConstants.USER_AGENT_VALUE)

        val requestUrl = chain.request().url.toString()
        if (requestUrl.endsWith("/associate")) {
            return chain.proceed(
                requestBuilderWithDeviceId.build()
            )
        }

        val response = requestBuilderWithDeviceId
            .addHeader(FabriikApiConstants.HEADER_AUTHORIZATION, SessionHolder.getSessionKey())
            .build()
            .run(chain::proceed)

        UserSessionManager.checkIfSessionExpired(
            scope = scope,
            context = context,
            response = response
        )

        return response
    }
}