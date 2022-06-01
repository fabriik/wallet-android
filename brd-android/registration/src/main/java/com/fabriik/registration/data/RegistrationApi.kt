package com.fabriik.registration.data

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
        sessionKey = "qvrphn76s49t9j3tmff4jm24a7m116dc5k6t41cd",
        dateHeader = dateHeader,
        signatureHeader = signature
    )

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
                .baseUrl(/*FabriikApiConstants.HOST_AUTH_API*/"https://567f-2a00-ee2-2607-1300-61a3-8d21-5842-cb14.ngrok.io/blocksatoshi/one/auth/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(RegistrationService::class.java)
        )
    }
}