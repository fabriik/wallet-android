package com.fabriik.registration.data

import com.fabriik.registration.data.requests.AssociateConfirmRequest
import com.fabriik.registration.data.requests.AssociateRequest
import okhttp3.ResponseBody
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateAccount(
        @HeaderMap headers: Map<String, String>,
        @Body request: AssociateRequest
    ) : ResponseBody

    @POST("associate/confirm")
    suspend fun associateAccountConfirm(
        @Body request: AssociateConfirmRequest
    ) : ResponseBody

    @POST("associate/resend")
    suspend fun resendAssociateAccountChallenge() : ResponseBody
}