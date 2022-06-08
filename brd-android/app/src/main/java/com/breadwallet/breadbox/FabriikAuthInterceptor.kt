package com.breadwallet.breadbox

import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.FabriikApiConstants
import com.platform.tools.SessionHolder
import okhttp3.Interceptor
import okhttp3.Response

class FabriikAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilderWithDeviceId = chain.request()
            .newBuilder()
            .addHeader("X-Device-ID", BRSharedPrefs.getDeviceId())

        val requestUrl = chain.request().url.toString()
        if (!requestUrl.startsWith(FabriikApiConstants.HOST_BLOCKSATOSHI_API)) {
            return chain.proceed(
                requestBuilderWithDeviceId.build()
            )
        }

        return requestBuilderWithDeviceId
            .addHeader("Authorization", SessionHolder.retrieveSession())
            .build()
            .run(chain::proceed)
    }
}