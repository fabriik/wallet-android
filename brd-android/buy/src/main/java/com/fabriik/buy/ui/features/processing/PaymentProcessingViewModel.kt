package com.fabriik.buy.ui.features.processing

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fabriik.buy.data.BuyApi
import com.fabriik.buy.data.enums.PaymentStatus
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

class PaymentProcessingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<PaymentProcessingContract.State, PaymentProcessingContract.Event, PaymentProcessingContract.Effect>(
    application, savedStateHandle
), KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()

    private val currentProcessingState: PaymentProcessingContract.State.Processing?
        get() = state.value as PaymentProcessingContract.State.Processing?

    private val currentCompletedState: PaymentProcessingContract.State.PaymentCompleted?
        get() = state.value as PaymentProcessingContract.State.PaymentCompleted?

    private lateinit var arguments: PaymentProcessingFragmentArgs

    init {
        handlePaymentFlow()
    }

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = PaymentProcessingFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentProcessingContract.State.Processing(
        arguments.paymentReference
    )

    override fun handleEvent(event: PaymentProcessingContract.Event) {
        when (event) {
            PaymentProcessingContract.Event.BackToHomeClicked ->
                setEffect { PaymentProcessingContract.Effect.Dismiss }

            PaymentProcessingContract.Event.ContactSupportClicked ->
                setEffect { PaymentProcessingContract.Effect.ContactSupport }

            PaymentProcessingContract.Event.TryDifferentMethodClicked ->
                setEffect { PaymentProcessingContract.Effect.BackToBuy }

            PaymentProcessingContract.Event.OnPaymentRedirectResult ->
                handlePaymentRedirectResult()

            PaymentProcessingContract.Event.PurchaseDetailsClicked -> {
                val purchaseId = currentCompletedState?.paymentReference
                if (!purchaseId.isNullOrBlank()) {
                    setEffect { PaymentProcessingContract.Effect.GoToPurchaseDetails(purchaseId) }
                }
            }
        }
    }

    private fun handlePaymentFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(INITIAL_DELAY)

            val redirectUrl = arguments.redirectUrl
            if (redirectUrl.isNullOrBlank()) {
                checkPaymentStatus()
            } else {
                setEffect { PaymentProcessingContract.Effect.OpenPaymentRedirect(redirectUrl) }
            }
        }
    }

    private fun handlePaymentRedirectResult() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(INITIAL_DELAY)
            checkPaymentStatus()
        }
    }

    private fun checkPaymentStatus() {
        val reference = currentProcessingState?.paymentReference ?: return

        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { buyApi.getPaymentStatus(reference) },
            callback = {
                setState {
                    if (it.data == PaymentStatus.CAPTURED || it.data == PaymentStatus.CARD_VERIFIED) {
                        PaymentProcessingContract.State.PaymentCompleted(reference)
                    } else {
                        PaymentProcessingContract.State.PaymentFailed(reference)
                    }
                }
            }
        )
    }

    companion object {
        private const val INITIAL_DELAY = 1000L
    }
}