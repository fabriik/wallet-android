package com.fabriik.trade.data

import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.data.response.SupportedTradingPairsResponse
import retrofit2.http.*

interface SwapService {

    @GET("supported-currencies")
    suspend fun getSupportedCurrencies(): SupportedTradingPairsResponse

    @GET("quote")
    suspend fun getQuote(
        @Query("security") security: String
    ): QuoteResponse
}