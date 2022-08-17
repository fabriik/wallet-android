package com.fabriik.buy.data

import com.fabriik.buy.data.request.AddPaymentInstrumentRequest
import com.fabriik.buy.data.response.AddPaymentInstrumentResponse
import com.fabriik.buy.data.response.PaymentInstrumentsResponse
import com.fabriik.buy.data.response.ReservationUrlResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface BuyService {

    @POST("payment-instruments")
    suspend fun addPaymentInstrument(
        @Body request: AddPaymentInstrumentRequest
    ): AddPaymentInstrumentResponse

    @GET("payment-instruments")
    suspend fun getPaymentInstruments(): PaymentInstrumentsResponse

    //Todo: remove /reserve
    @GET("reserve")
    suspend fun getPaymentUrl(
        @Header("Authorization") auth: String,
        @Query("test") isTestNetwork: Boolean
    ): ReservationUrlResponse
}