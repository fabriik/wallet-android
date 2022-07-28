package com.fabriik.buy.ui.features.processing

import android.app.Application
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import kotlinx.coroutines.delay

class PaymentProcessingViewModel(
    application: Application
) : FabriikViewModel<PaymentProcessingContract.State, PaymentProcessingContract.Event, PaymentProcessingContract.Effect>(
    application
) {

    init {
        loadData()
    }

    override fun createInitialState() = PaymentProcessingContract.State.Processing

    override fun handleEvent(event: PaymentProcessingContract.Event) {
        when (event) {
            PaymentProcessingContract.Event.BackToHomeClicked ->
                setEffect { PaymentProcessingContract.Effect.Dismiss }

            PaymentProcessingContract.Event.ContactSupportClicked ->
                setEffect { PaymentProcessingContract.Effect.ContactSupport }

            PaymentProcessingContract.Event.PurchaseDetailsClicked -> {
                val purchaseId = (currentState as PaymentProcessingContract.State.Loaded?)?.purchaseId
                if (!purchaseId.isNullOrBlank()) {
                    setEffect { PaymentProcessingContract.Effect.GoToPurchaseDetails(purchaseId) }
                }
            }
        }
    }

    private fun loadData() {
        callApi(
            endState = {currentState},
            startState = {currentState},
            action = { dummyApiCall() },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        setState {
                            PaymentProcessingContract.State.Loaded(
                                PaymentProcessingContract.Status.SUCCESS, requireNotNull(it.data)
                            )
                        }

                    Status.ERROR ->
                        setState {
                            PaymentProcessingContract.State.Loaded(
                                PaymentProcessingContract.Status.FAILED, null
                            )
                        }
                }
            }
        )
    }

    private suspend fun dummyApiCall(): Resource<String?> {
        delay(3000)
        //todo: replace with actual API call

        return Resource.success(
            data = "mocked_purchase_id" //todo: pass id from API
        )
    }
}