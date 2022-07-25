package com.fabriik.trade.data

import com.fabriik.trade.data.model.SwapTransactionData
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

    fun getSwapByHash(hash: String) : SwapTransactionData? = swapTransactions.find {
        it.depositHash == hash || it.withdrawalHash == hash
    }

    fun getPendingSwapWithdrawals(currency: String): List<SwapTransactionData> {
        return swapTransactions.filter {
            it.withdrawalCurrency.equals(currency, true) && it.withdrawalHash == null
        }
    }
}