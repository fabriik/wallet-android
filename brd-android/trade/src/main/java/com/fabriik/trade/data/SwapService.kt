package com.fabriik.trade.data

import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.data.response.TradingPairsResponse
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getTradingPairs(): TradingPairsResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("security") security: String
    ): QuoteResponse
}