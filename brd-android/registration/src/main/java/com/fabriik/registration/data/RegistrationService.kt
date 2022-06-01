package com.fabriik.registration.data

import com.fabriik.registration.data.requests.AssociateRequest
import okhttp3.ResponseBody
import retrofit2.http.*

interface RegistrationService {

    @POST("associate")
    suspend fun associateAccount(
        @Header("Date") dateHeader: String,
        @Header("Authorization") sessionKey: String,
        @Header("Signature") signatureHeader: String,
        @Body request: AssociateRequest
    ) : ResponseBody
}