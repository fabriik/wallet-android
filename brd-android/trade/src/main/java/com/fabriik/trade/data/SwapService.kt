package com.fabriik.trade.data

import com.fabriik.trade.data.request.CreateOrderRequest
import com.fabriik.trade.data.request.EstimateEthFeeRequest
import com.fabriik.trade.data.response.*
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedCurrenciesResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("security") security: String
    ): QuoteResponse

    @POST("create")
    suspend fun createOrder(
        @Body body: CreateOrderRequest
    ): CreateOrderResponse

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