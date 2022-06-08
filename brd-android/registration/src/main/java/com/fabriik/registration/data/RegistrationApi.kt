package com.fabriik.registration.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import com.fabriik.registration.data.responses.AssociateResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RegistrationApi(
    private val context: Context,
    private val service: RegistrationService
) {

    private val responseMapper = FabriikApiResponseMapper()

    suspend fun associateAccount(
        email: String, token: String, headers: Map<String, String?>
    ): Resource<AssociateResponse?> {
        return try {
            val response = service.associateAccount(
                request = AssociateRequest(
                    email = email,
                    token = token
                ),
                headers = headers
            )
            responseMapper.mapSuccess(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex,
                kClass = AssociateRequest::class
            )
        }
    }

    suspend fun associateAccountConfirm(code: String): Resource<ResponseBody?> {
        return try {
            val response = service.associateAccountConfirm(AssociateConfirmRequest(code))
            Resource.success(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex,
                kClass = ResponseBody::class
            )
        }
    }

    suspend fun resendAssociateAccountChallenge(): Resource<ResponseBody?> {
        return try {
            val response = service.resendAssociateAccountChallenge()
            Resource.success(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex,
                kClass = ResponseBody::class
            )
        }
    }

    companion object {

        fun create(context: Context) = RegistrationApi(
            context = context,
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