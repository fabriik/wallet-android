package com.fabriik.trade.data

import com.fabriik.trade.data.request.CreateSwapOrderRequest
import com.fabriik.trade.data.request.EstimateEthFeeRequest
import com.fabriik.trade.data.response.*
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedCurrenciesResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("from") from: String,
        @Query("to") to: String,
    ): QuoteResponse

    @POST("create")
    suspend fun createOrder(
        @Body body: CreateSwapOrderRequest
    ): CreateSwapOrderResponse

    @GET("exchange/{exchangeId}")
    suspend fun getExchange(
        @Path("exchangeId") exchangeId: String
    ): ExchangeOrder

    @GET("exchanges")
    suspend fun getExchanges(): ExchangesResponse

    @POST("estimate-fee")
    suspend fun estimateEthFee(
        @Body body: EstimateEthFeeRequest
    ): EstimateEthFeeResponse
}
