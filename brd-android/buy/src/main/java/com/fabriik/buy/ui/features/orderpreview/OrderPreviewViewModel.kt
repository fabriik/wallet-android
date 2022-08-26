package com.fabriik.buy.ui.features.orderpreview

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.buy.data.enums.PaymentStatus
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.toBundle
import com.fabriik.trade.ui.features.swap.SwapInputHelper
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.direct
import org.kodein.di.erased.instance

class OrderPreviewViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<OrderPreviewContract.State, OrderPreviewContract.Event, OrderPreviewContract.Effect>(
    application, savedStateHandle
), KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private val helper = SwapInputHelper(
        direct.instance(), direct.instance(), direct.instance()
    )

    private lateinit var arguments: OrderPreviewFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = OrderPreviewFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = OrderPreviewContract.State(
        fiatAmount = arguments.fiatAmount,
        networkFee = arguments.networkFee,
        fiatCurrency = arguments.fiatCurrency,
        quoteResponse = arguments.quoteResponse,
        cryptoCurrency = arguments.cryptoCurrency,
        paymentInstrument = arguments.paymentInstrument
    )

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
                setEffect { OrderPreviewContract.Effect.RequestUserAuthentication }

            OrderPreviewContract.Event.OnUserAuthenticationSucceed ->
                createBuyOrder()

            OrderPreviewContract.Event.OnPaymentRedirectResult ->
                checkPaymentStatus()

            is OrderPreviewContract.Event.OnSecurityCodeChanged ->
                setState { copy(securityCode = event.securityCode).validate() }
        }
    }

    private fun createBuyOrder() {
        val quoteResponse = requireNotNull(currentState.quoteResponse)
        if (quoteResponse.isExpired()) {
            setEffect { OrderPreviewContract.Effect.TimeoutScreen }
            return
        }

        callApi(
            endState = { currentState },
            startState = { currentState },  //todo: payment processing screen
            action = {
                val destinationAddress =
                    helper.loadAddress(currentState.cryptoCurrency)?.toString() ?: return@callApi Resource.error(
                        message = getString(R.string.FabriikApi_DefaultError)
                    )

                buyApi.createOrder(
                    quoteId = quoteResponse.quoteId,
                    baseQuantity = currentState.fiatAmount,
                    termQuantity = currentState.cryptoAmount,
                    destination = destinationAddress,
                    nologCvv = currentState.securityCode,
                    sourceInstrumentId = currentState.paymentInstrument.id
                )
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = requireNotNull(it.data)
                        val reference = response.paymentReference
                        val redirectUrl = response.redirectUrl

                        setState { copy(paymentReference = reference) }

                        if (redirectUrl.isNullOrBlank()) {
                            checkPaymentStatus()
                        } else {
                            setEffect { OrderPreviewContract.Effect.OpenPaymentRedirect(redirectUrl) }
                        }
                    }

                    Status.ERROR ->
                        setEffect {
                            OrderPreviewContract.Effect.ShowError(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
            }
        )
    }

    private fun checkPaymentStatus() {
        val reference = currentState.paymentReference ?: return

        // todo: show payment processing screen
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.getPaymentStatus(reference) },
            callback = {
                setEffect {
                    OrderPreviewContract.Effect.PaymentProcessing(
                        if (it.data == PaymentStatus.CAPTURED || it.data == PaymentStatus.CARD_VERIFIED) {
                            reference
                        } else {
                            null
                        }
                    )
                }
            }
        )
    }

    private fun OrderPreviewContract.State.validate() = copy(
        confirmButtonEnabled = securityCode.length == 3
    )
}