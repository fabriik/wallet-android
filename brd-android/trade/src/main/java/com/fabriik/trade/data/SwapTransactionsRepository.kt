package com.fabriik.trade.data

import com.fabriik.trade.data.model.SwapBuyTransactionData
import com.fabriik.trade.data.response.ExchangeOrderStatus
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SwapTransactionsRepository {

    private val swapBuyTransactions: MutableList<SwapBuyTransactionData> = mutableListOf()
    private val changeEventChannel = BroadcastChannel<Unit>(CONFLATED)

    fun updateData(data: List<SwapBuyTransactionData>) {
        swapBuyTransactions.clear()
        swapBuyTransactions.addAll(data)
        changeEventChannel.offer(Unit)
    }

    fun changes(): Flow<Unit> = changeEventChannel.asFlow()

    fun getDataByHash(hash: String): SwapBuyTransactionData? = swapBuyTransactions.find {
        it.source.transactionId == hash || it.destination.transactionId == hash
    }

    fun getUnlinkedTransactionData(currency: String): List<SwapBuyTransactionData> {
        return swapBuyTransactions.filter {
            it.destination.currency.equals(currency, true)
                && it.destination.transactionId == null
                && it.exchangeStatus != ExchangeOrderStatus.FAILED
        }
    }

    fun isAnySwapPendingForSourceCurrency(sourceCurrency: String): Boolean {
        return swapBuyTransactions
            .filter { it.isSwapTransaction() }
            .filter { it.source.currency.equals(sourceCurrency, true) }
            .any { it.exchangeStatus == ExchangeOrderStatus.PENDING }
    }
}