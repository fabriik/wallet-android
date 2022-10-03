package com.fabriik.buy.data

import com.fabriik.buy.data.request.AddPaymentInstrumentRequest
import com.fabriik.buy.data.request.CreateBuyOrderRequest
import com.fabriik.buy.data.response.*
import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.data.response.SupportedCurrenciesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface BuyService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedCurrenciesResponse

    @POST("payment-instrument")
    suspend fun addPaymentInstrument(
        @Body request: AddPaymentInstrumentRequest
    ): AddPaymentInstrumentResponse

    @GET("payment-instruments")
    suspend fun getPaymentInstruments(): PaymentInstrumentsResponse

    @DELETE("payment-instrument")
    suspend fun deletePaymentInstrument(@Query("instrument_id") instrumentId: String): Response<Unit>

    @GET("payment-status")
    suspend fun getPaymentStatus(
        @Query("reference") reference: String
    ): PaymentStatusResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("from") from: String,
        @Query("to") to: String,
    ): QuoteResponse

    @POST("create")
    suspend fun createOrder(
        @Body body: CreateBuyOrderRequest
    ): CreateBuyOrderResponse
}