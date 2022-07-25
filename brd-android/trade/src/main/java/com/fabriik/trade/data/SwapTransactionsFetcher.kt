package com.fabriik.trade.data

import android.util.Log
import com.fabriik.common.data.Status
import com.fabriik.trade.data.model.SwapTransactionData
import kotlinx.coroutines.*

private const val REFRESH_DELAY_MS = 60_000L

class SwapTransactionsFetcher(
    private val swapApi: SwapApi,
    private val transactionsRepository: SwapTransactionsRepository
) {

    fun start(scope: CoroutineScope) {
        scope.launch {
            while (isActive) {
                updateData()
                delay(REFRESH_DELAY_MS)
            }
        }
    }

    private suspend fun updateData() {
        val transactions = swapApi.getSwapTransactions()
        val transactionsData = transactions.data ?: emptyList()
        if (transactions.status == Status.ERROR || transactionsData.isEmpty()) {
            return
        }

        transactionsRepository.updateData(transactionsData)
    }
}