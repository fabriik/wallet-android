package com.fabriik.registration.data

import android.content.Context
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.registration.utils.UserSessionManager
import kotlinx.coroutines.CoroutineScope
import okhttp3.Interceptor
import okhttp3.Response

class RegistrationApiInterceptor(
    private val context: Context,
    private val scope: CoroutineScope
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilderWithDeviceId = chain.request()
            .newBuilder()
            .addHeader("X-Device-ID", BRSharedPrefs.getDeviceId())

        val requestUrl = chain.request().url.toString()
        if (requestUrl.endsWith("/associate")) {
            return chain.proceed(
                requestBuilderWithDeviceId.build()
            )
        }

        val response = requestBuilderWithDeviceId
            .addHeader("Authorization", ""/*SessionHolder.getSessionKey()*/)
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