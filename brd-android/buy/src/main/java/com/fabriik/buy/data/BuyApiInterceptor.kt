package com.fabriik.buy.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import com.fabriik.trade.data.SwapApiInterceptor

class BuyApiInterceptor(
    context: Context, scope: CoroutineScope
) : SwapApiInterceptor(context, scope)