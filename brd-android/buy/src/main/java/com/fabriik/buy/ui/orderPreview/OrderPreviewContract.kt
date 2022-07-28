package com.fabriik.buy.ui.orderPreview

import com.fabriik.common.ui.base.FabriikContract

class OrderPreviewContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
        object OnTermsAndConditionsCLicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()

        data class InfoDialog(val type: DialogType) : Effect()
    }

    object State : FabriikContract.State
}

enum class DialogType {
    CREDIT_CARD_FEE,
    NETWORK_FEE
}