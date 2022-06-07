package com.fabriik.kyc.data

import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.data.response.CountriesResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class KycApi(
    private val service: KycService
) {
    suspend fun getCountries(): Resource<List<Country>?> {
        return try {
            val response = service.getCountries(Locale.getDefault().language)
            Resource.success(data = response.countries)
        } catch (ex: Exception) {
            Resource.error(message = ex.message ?: "") //todo: default error
        }
    }

    companion object {

        fun create() = KycApi(
            service = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(KycApiInterceptor())
                        .build()
                )
                .baseUrl(FabriikApiConstants.HOST_KYC_API)
                .addConverterFactory(
                    MoshiConverterFactory.create(
                        Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                    )
                )
                .build()
                .create(KycService::class.java)
        )
    }
}