package com.fabriik.kyc.data

import com.breadwallet.ext.addUniqueHeader
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.FabriikApiConstants
import com.platform.tools.SessionHolder
import okhttp3.Interceptor
import okhttp3.Response

class KycApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {
        val request = chain.request()
        return request
            .newBuilder()
            .addUniqueHeader(request, FabriikApiConstants.HEADER_DEVICE_ID, BRSharedPrefs.getDeviceId())
            .addUniqueHeader(request, FabriikApiConstants.HEADER_USER_AGENT, FabriikApiConstants.USER_AGENT_VALUE)
            .addUniqueHeader(request, FabriikApiConstants.HEADER_AUTHORIZATION, SessionHolder.getSessionKey())
            .build()
            .run(chain::proceed)
    }
}