package com.fabriik.kyc.data

import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.FabriikApiConstants
import com.platform.tools.SessionHolder
import okhttp3.Interceptor

class KycApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) = chain.request()
        .newBuilder()
        .addHeader(FabriikApiConstants.HEADER_DEVICE_ID, BRSharedPrefs.getDeviceId())
        .addHeader(FabriikApiConstants.HEADER_USER_AGENT, FabriikApiConstants.USER_AGENT_VALUE)
        .addHeader(FabriikApiConstants.HEADER_AUTHORIZATION, SessionHolder.getSessionKey())
        .build()
        .run(chain::proceed)
}