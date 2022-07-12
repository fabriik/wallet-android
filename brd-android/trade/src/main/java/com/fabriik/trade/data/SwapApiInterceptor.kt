package com.fabriik.trade.data

import com.breadwallet.tools.manager.BRSharedPrefs
import com.platform.tools.SessionHolder
import okhttp3.Interceptor

class SwapApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.request()
        .newBuilder()
        .addHeader("X-Device-ID", BRSharedPrefs.getDeviceId())
        .addHeader("Authorization", SessionHolder.getSessionKey())
        .build()
        .run(chain::proceed)
}