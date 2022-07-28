package com.fabriik.buy.ui.features.processing

import com.fabriik.buy.R
import com.fabriik.common.ui.base.FabriikContract

class PaymentProcessingContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackToHomeClicked : Event()
        object ContactSupportClicked: Event()
        object PurchaseDetailsClicked: Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object ContactSupport : Effect()
        data class GoToPurchaseDetails(val purchaseId: String) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Processing : State()
        data class Loaded(val status: Status, val purchaseId: String?) : State()
    }

    enum class Status(
        val icon: Int,
        val title: Int,
        val description: Int,
        val contactSupportVisible: Boolean,
        val purchaseDetailsVisible: Boolean
    ) {
        SUCCESS(
            icon = R.drawable.ic_payment_succeed,
            title = R.string.Buy_ProcessingSucceed_Title,
            description = R.string.Buy_ProcessingSucceed_Description,
            contactSupportVisible = false,
            purchaseDetailsVisible = true
        ),

        FAILED(
            icon = R.drawable.ic_payment_failed,
            title = R.string.Buy_ProcessingFailed_Title,
            description = R.string.Buy_ProcessingFailed_Description,
            contactSupportVisible = true,
            purchaseDetailsVisible = false
        )
    }
}