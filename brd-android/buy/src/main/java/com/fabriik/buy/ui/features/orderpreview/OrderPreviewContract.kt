package com.fabriik.buy.ui.features.orderpreview

import com.fabriik.common.ui.base.FabriikContract

class OrderPreviewContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
        object OnTermsAndConditionsCLicked : Event()
        object OnUserAuthenticationSucceed : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object PaymentProcessing : Effect()
        object RequestUserAuthentication : Effect()

        data class ShowInfoDialog(val type: DialogType) : Effect()
    }

    object State : FabriikContract.State
}

enum class DialogType {
    CREDIT_CARD_FEE,
    NETWORK_FEE
}