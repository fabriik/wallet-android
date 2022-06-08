package com.fabriik.kyc.data

import com.breadwallet.tools.manager.BRSharedPrefs
import com.platform.tools.SessionHolder
import okhttp3.Interceptor

class KycApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.request()
        .newBuilder()
        .addHeader("X-Device-ID", BRSharedPrefs.getDeviceId())
        .addHeader("sessionKey", SessionHolder.retrieveSession())
        .build()
        .run(chain::proceed)
}