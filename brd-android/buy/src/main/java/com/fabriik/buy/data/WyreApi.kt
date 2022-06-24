package com.fabriik.buy.data

import com.fabriik.common.data.FabriikApiConstants
import com.platform.tools.SessionHolder
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WyreApi(private val service: WyreService) {

    suspend fun getPaymentUrl(isTestNetwork: Boolean) = service.getPaymentUrl(
        auth = SessionHolder.getSessionKey(),
        isTestNetwork = isTestNetwork
    )

    companion object {

        const val REDIRECT_URL = "https://fabriikw.com/success"
        const val FAILURE_REDIRECT_URL = "https://fabriikw.com/error"

        fun create() = WyreApi(
            Retrofit.Builder()
                .baseUrl(FabriikApiConstants.HOST_WYRE_API)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(WyreService::class.java)
        )
    }
}