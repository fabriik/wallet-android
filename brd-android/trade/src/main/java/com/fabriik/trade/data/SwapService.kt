package com.fabriik.trade.data

import com.fabriik.trade.data.request.CreateOrderRequest
import com.fabriik.trade.data.response.CreateOrderResponse
import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.data.response.TradingPairsResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getTradingPairs(): TradingPairsResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("security") security: String
    ): QuoteResponse

    @POST("create")
    suspend fun createOrder(
        @Body body: CreateOrderRequest
    ): CreateOrderResponse
}