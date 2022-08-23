package com.fabriik.trade.data

import com.fabriik.trade.data.model.SwapTransactionData
import com.fabriik.trade.data.response.ExchangeOrderStatus
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SwapTransactionsRepository {

    private val swapTransactions: MutableList<SwapTransactionData> = mutableListOf()
    private val changeEventChannel = BroadcastChannel<Unit>(CONFLATED)

    fun updateData(data: List<SwapTransactionData>) {
        swapTransactions.clear()
        swapTransactions.addAll(data)
        changeEventChannel.offer(Unit)
    }

    fun changes(): Flow<Unit> = changeEventChannel.asFlow()

    fun getSwapByHash(hash: String): SwapTransactionData? = swapTransactions.find {
        it.source.transactionId == hash || it.destination.transactionId == hash
    }

    fun getUnlinkedSwapWithdrawals(currency: String): List<SwapTransactionData> {
        return swapTransactions.filter {
            it.destination.currency.equals(currency, true) && it.destination.transactionId == null
        }
    }

    fun isAnySwapPendingForSourceCurrency(sourceCurrency: String): Boolean {
        return swapTransactions
            .filter { it.source.currency.equals(sourceCurrency, true) }
            .any { it.exchangeStatus == ExchangeOrderStatus.PENDING }
    }
}