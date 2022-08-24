package com.fabriik.buy.ui.features.orderpreview

import com.fabriik.common.ui.base.FabriikContract

class OrderPreviewContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
        object OnSecurityCodeInfoClicked : Event()
        object OnTermsAndConditionsClicked : Event()
        object OnUserAuthenticationSucceed : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object PaymentProcessing : Effect()
        object RequestUserAuthentication : Effect()

        data class ShowInfoDialog(
            val image: Int? = null,
            val title: Int,
            val description: Int
        ) : Effect()

        data class OpenWebsite(val url: String): Effect()
    }

    data class State(
        val securityCode: String = ""
    ) : FabriikContract.State
}