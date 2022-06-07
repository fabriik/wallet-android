package com.fabriik.registration.data

import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RegistrationApi(
    private val service: RegistrationService
) {

    suspend fun associateAccount(email: String, token: String, headers: Map<String, String?>) =
        service.associateAccount(
            request = AssociateRequest(
                email = email,
                token = token
            ),
            headers = headers
        )

    suspend fun associateAccountConfirm(code: String) =
        service.associateAccountConfirm(AssociateConfirmRequest(code))

    suspend fun resendAssociateAccountChallenge() =
        service.resendAssociateAccountChallenge()

    companion object {

        fun create() = RegistrationApi(
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(RegistrationApiInterceptor())
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_AUTH_API)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
                .create(RegistrationService::class.java)
        )
    }
}