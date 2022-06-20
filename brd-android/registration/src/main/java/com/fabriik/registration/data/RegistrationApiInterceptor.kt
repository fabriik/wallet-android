package com.fabriik.registration.data

import com.breadwallet.tools.manager.BRSharedPrefs
import com.platform.tools.SessionHolder
import okhttp3.Interceptor
import okhttp3.Response

class RegistrationApiInterceptor : Interceptor {

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

        return requestBuilderWithDeviceId
            .addHeader("Authorization", ""/*SessionHolder.getSessionKey()*/)
            .build()
            .run(chain::proceed)
    }
}