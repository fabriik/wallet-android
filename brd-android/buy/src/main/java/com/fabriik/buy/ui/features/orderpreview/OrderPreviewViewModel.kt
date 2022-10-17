package com.fabriik.buy.ui.features.orderpreview

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
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
), OrderPreviewEventHandler, KodeinAware {

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

    override fun onBackClicked() {
        setEffect { OrderPreviewContract.Effect.Back }
    }

    override fun onDismissClicked() {
        setEffect { OrderPreviewContract.Effect.Dismiss }
    }

    override fun onConfirmClicked() {
        setEffect { OrderPreviewContract.Effect.RequestUserAuthentication }
    }

    override fun onCreditInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Swap_CardFee,
                description = R.string.Buy_CardFee
            )
        }
    }

    override fun onNetworkInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Buy_NetworkFees,
                description = R.string.Buy_NetworkFeeMessage
            )
        }
    }

    override fun onSecurityCodeInfoClicked() {
        setEffect {
            OrderPreviewContract.Effect.ShowInfoDialog(
                image = R.drawable.ic_info_cvv,
                title = R.string.Buy_SecurityCode,
                description = R.string.Buy_SecurityCodePopup
            )
        }
    }

    override fun onTermsAndConditionsClicked() {
        setEffect {
            OrderPreviewContract.Effect.OpenWebsite(
                FabriikApiConstants.URL_TERMS_AND_CONDITIONS
            )
        }
    }

    override fun onUserAuthenticationSucceed() {
        val quoteResponse = requireNotNull(currentState.quoteResponse)
        if (quoteResponse.isExpired()) {
            setEffect { OrderPreviewContract.Effect.TimeoutScreen }
            return
        }

        callApi(
            endState = { copy(fullScreenLoadingIndicator = false) },
            startState = { copy(fullScreenLoadingIndicator = true) },
            action = {
                val destinationAddress =
                    helper.loadAddress(currentState.cryptoCurrency)?.toString() ?: return@callApi Resource.error(
                        message = getString(R.string.Api_DefaultError)
                    )

                buyApi.createOrder(
                    quoteId = quoteResponse.quoteId,
                    baseQuantity = currentState.totalFiatAmount,
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

                        setEffect {
                            OrderPreviewContract.Effect.PaymentProcessing(
                                paymentReference = reference,
                                redirectUrl = redirectUrl
                            )
                        }
                    }

                    Status.ERROR -> {
                        setState { copy(confirmButtonEnabled = false) }
                        setEffect {
                            if (getString(R.string.Swap_RequestTimedOut) == it.message) {
                                OrderPreviewContract.Effect.TimeoutScreen
                            } else {
                                OrderPreviewContract.Effect.ShowError(
                                    it.message ?: getString(R.string.Api_DefaultError)
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    override fun onSecurityCodeChanged(securityCode: String) {
        setState { copy(securityCode = securityCode).validate() }
    }

    private fun OrderPreviewContract.State.validate() = copy(
        confirmButtonEnabled = securityCode.length == 3
    )
}