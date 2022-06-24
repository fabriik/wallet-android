package com.fabriik.buy.ui

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.fabriik.buy.data.WyreApi
import com.fabriik.common.data.Resource
import kotlinx.coroutines.Dispatchers

class BuyWebViewViewModel(
    private val api: WyreApi = WyreApi.create()
) : ViewModel(), LifecycleObserver {

    fun getPaymentUrl() = liveData(Dispatchers.IO) {
        try {
            emit(
                Resource.success(
                    data = api.getPaymentUrl(
                        false
                    )
                )
            )
        } catch (exception: Exception) {
            emit(
                Resource.error(
                    data = null,
                    message = exception.message ?: "Error Occurred!"
                )
            )
        }
    }
}