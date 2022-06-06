package com.fabriik.registration.data

import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RegistrationApi(private val service: RegistrationService) {

    suspend fun associateAccount(email: String, token: String, signature: String, dateHeader: String) = service.associateAccount(
        request = AssociateRequest(
            email = email,
            token = token
        ),
        headers = mapOf(
            Pair("Date", dateHeader),
            Pair("Signature", signature)
        )
    )

    suspend fun associateAccountConfirm(code: String) = service.associateAccountConfirm(
        request = AssociateConfirmRequest(code)
    )

    suspend fun resendAssociateAccountChallenge() = service.resendAssociateAccountChallenge()

    companion object {

        fun create() = RegistrationApi(
            Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_AUTH_API)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RegistrationService::class.java)
        )
    }
}