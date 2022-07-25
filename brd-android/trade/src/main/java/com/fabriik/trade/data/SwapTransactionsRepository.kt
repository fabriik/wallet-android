package com.fabriik.trade.data

import com.fabriik.trade.data.model.SwapTransactionData
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.concurrent.ConcurrentHashMap

class SwapTransactionsRepository {

    private val swapTransactions = ConcurrentHashMap<String, SwapTransactionData>()
    private val changeEventChannel = BroadcastChannel<Unit>(CONFLATED)

    fun updateData(data: Map<String, SwapTransactionData>) {
        swapTransactions.clear()
        swapTransactions.putAll(data)
        changeEventChannel.offer(Unit)
    }

    fun changes(): Flow<Unit> = changeEventChannel.asFlow()

    fun getSwapDataByTransactionId(transactionId: String) = swapTransactions[transactionId]
}