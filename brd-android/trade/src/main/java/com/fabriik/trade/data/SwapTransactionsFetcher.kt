package com.fabriik.trade.data

import com.fabriik.common.data.Status
import kotlinx.coroutines.*

private const val REFRESH_DELAY_MS = 60_000L

class SwapTransactionsFetcher(
    private val swapApi: SwapApi,
    val transactionsRepository: SwapTransactionsRepository
) {

    private lateinit var scope: CoroutineScope

    fun start(scope: CoroutineScope) {
        this.scope = scope

        scope.launch {
            while (isActive) {
                updateData()
                delay(REFRESH_DELAY_MS)
            }
        }
    }

    fun refreshData() {
        if (scope.isActive) {
            scope.launch {
                updateData()
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