package com.fabriik.kyc.data

import com.fabriik.kyc.data.response.CountriesResponse
import retrofit2.http.*

interface KycService {

    @GET("countries")
    suspend fun getCountries(
        @Query("_locale") locale: String
    ): CountriesResponse
}