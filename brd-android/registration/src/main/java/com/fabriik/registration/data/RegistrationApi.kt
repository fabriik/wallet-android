package com.fabriik.registration.data

import android.content.Context
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.utils.FabriikApiResponseMapper
import com.fabriik.common.data.model.Profile
import com.fabriik.common.utils.adapter.BigDecimalAdapter
import com.fabriik.common.utils.adapter.CalendarJsonAdapter
import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateEmailRequest
import com.fabriik.registration.data.requests.AssociateNewDeviceRequest
import com.fabriik.registration.data.responses.AssociateEmailResponse
import com.fabriik.registration.data.responses.AssociateNewDeviceResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RegistrationApi(
    private val context: Context,
    private val service: RegistrationService
) {

    private val responseMapper = FabriikApiResponseMapper()

    suspend fun associateEmail(
        email: String, token: String, subscribe: Boolean, headers: Map<String, String?>
    ): Resource<AssociateEmailResponse?> {
        return try {
            val response = service.associateEmail(
                request = AssociateEmailRequest(
                    email = email,
                    token = token,
                    subscribe = subscribe,
                ),
                headers = headers
            )
            responseMapper.mapFabriikApiResponseSuccess(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun associateNewDevice(
        nonce: String, token: String, headers: Map<String, String?>
    ): Resource<AssociateNewDeviceResponse?> {
        return try {
            val response = service.associateNewDevice(
                request = AssociateNewDeviceRequest(
                    nonce = nonce,
                    token = token
                ),
                headers = headers
            )

            Resource.success(response)
        } catch (ex: Exception) {
            Resource.error(message = ex.message ?: "Not found")
        }
    }

    suspend fun associateAccountConfirm(code: String): Resource<ResponseBody?> {
        return try {
            val response = service.associateAccountConfirm(AssociateConfirmRequest(code))
            if (response.isSuccessful) {
                Resource.success(null)
            } else {
                Resource.error(message = "Invalid code")
            }
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun resendAssociateAccountChallenge(): Resource<ResponseBody?> {
        return try {
            val response = service.resendAssociateAccountChallenge()
            if (response.isSuccessful) {
                Resource.success(null)
            } else {
                Resource.error(message = "Code not sent")
            }
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun getProfile(): Resource<Profile?> {
        return try {
            val response = service.getProfile()
            Resource.success(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    suspend fun deleteProfile(): Resource<Unit?> {
        return try {
            val response = service.deleteProfile()
            Resource.success(response)
        } catch (ex: Exception) {
            responseMapper.mapError(
                context = context,
                exception = ex
            )
        }
    }

    companion object {

        fun create(context: Context, moshiConverter: MoshiConverterFactory, interceptor: RegistrationApiInterceptor) = RegistrationApi(
            context = context,
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(interceptor)
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_AUTH_API)
                .addConverterFactory(moshiConverter)
                .build()
                .create(RegistrationService::class.java)
        )
    }
}