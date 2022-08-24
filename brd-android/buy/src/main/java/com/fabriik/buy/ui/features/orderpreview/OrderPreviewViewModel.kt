package com.fabriik.buy.ui.features.orderpreview

import android.app.Application
import com.fabriik.buy.R
import com.fabriik.buy.ui.features.billingaddress.BillingAddressContract
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.ui.base.FabriikViewModel

class OrderPreviewViewModel(
    application: Application
) : FabriikViewModel<OrderPreviewContract.State, OrderPreviewContract.Event, OrderPreviewContract.Effect>(
    application
) {
    override fun createInitialState() = OrderPreviewContract.State()

    override fun handleEvent(event: OrderPreviewContract.Event) {
        when (event) {
            OrderPreviewContract.Event.OnBackPressed ->
                setEffect { OrderPreviewContract.Effect.Back }

            OrderPreviewContract.Event.OnDismissClicked ->
                setEffect { OrderPreviewContract.Effect.Dismiss }

            OrderPreviewContract.Event.OnCreditInfoClicked ->
                setEffect {
                    OrderPreviewContract.Effect.ShowInfoDialog(
                        title = R.string.Buy_OrderPreview_CardFeesDialog_Title,
                        description = R.string.Buy_OrderPreview_CardFeesDialog_Content
                    )
                }

            OrderPreviewContract.Event.OnNetworkInfoClicked ->
                setEffect {
                    OrderPreviewContract.Effect.ShowInfoDialog(
                        title = R.string.Buy_OrderPreview_NetworkFeesDialog_Title,
                        description = R.string.Buy_OrderPreview_NetworkFeesDialog_Content
                    )
                }

            OrderPreviewContract.Event.OnSecurityCodeInfoClicked ->
                setEffect {
                    OrderPreviewContract.Effect.ShowInfoDialog(
                        image = R.drawable.ic_info_cvv,
                        title = R.string.Buy_AddCard_CvvDialog_Title,
                        description = R.string.Buy_AddCard_CvvDialog_Content
                    )
                }

            OrderPreviewContract.Event.OnTermsAndConditionsClicked ->
                setEffect {
                    OrderPreviewContract.Effect.OpenWebsite(
                        FabriikApiConstants.URL_TERMS_AND_CONDITIONS
                    )
                }

            OrderPreviewContract.Event.OnConfirmClicked ->
                //todo: API call?
                setEffect { OrderPreviewContract.Effect.RequestUserAuthentication }

            OrderPreviewContract.Event.OnUserAuthenticationSucceed ->
                createBuyOrder()
        }
    }

    private fun createBuyOrder() {
        //todo: API call
        setEffect { OrderPreviewContract.Effect.PaymentProcessing }
    }
}