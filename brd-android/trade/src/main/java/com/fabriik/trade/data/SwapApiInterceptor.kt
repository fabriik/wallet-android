package com.fabriik.trade.data

import android.content.Context
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.registration.utils.UserSessionManager
import com.platform.tools.SessionHolder
import kotlinx.coroutines.CoroutineScope
import okhttp3.Interceptor
import okhttp3.Response

class SwapApiInterceptor(
    private val context: Context,
    private val scope: CoroutineScope
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {
        val response = chain.request()
            .newBuilder()
            .addHeader("X-Device-ID", BRSharedPrefs.getDeviceId())
            .addHeader("Authorization", SessionHolder.getSessionKey())
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