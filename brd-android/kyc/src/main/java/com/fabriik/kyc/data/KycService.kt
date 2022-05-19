package com.fabriik.kyc.data

import okhttp3.ResponseBody
import retrofit2.http.POST

interface KycService {

    @POST("auth/register")
    suspend fun register() : ResponseBody

    @POST("kyc/exposed-person")
    suspend fun exposedPersonData() : ResponseBody
}