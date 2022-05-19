package com.fabriik.kyc.data

import com.fabriik.common.data.FabriikApiConstants
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class KycApi(val service: KycService) {

    suspend fun register(
        firstName: String,
        lastName: String,
        country: String,
        email: String,
        password: String
    ) = service.register()

    suspend fun exposedPersonData(
        firstName: String,
        lastName: String,
        country: String,
        email: String,
        password: String
    ) = service.exposedPersonData()

    companion object {

        fun create() = KycApi(
            service = Retrofit.Builder()
                .baseUrl(FabriikApiConstants.HOST_ONE_API)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(KycService::class.java)
        )
    }
}