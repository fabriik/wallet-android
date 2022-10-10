package com.fabriik.buy.ui.features.buydetails

import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.data.enums.BuyDetailsFlow
import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.buy.ui.features.processing.testEffect
import com.fabriik.buy.ui.features.processing.toMap
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.trade.data.response.ExchangeOrder
import com.fabriik.trade.data.response.ExchangeOrderStatus
import com.fabriik.trade.data.response.ExchangeSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
@Config(application = TestFabriikApplication::class)
class BuyDetailsViewModelTest {

    private lateinit var viewModel: BuyDetailsViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    private val exchangeOrder = ExchangeOrder(
        orderId = CURRENT_EXCHANGE_ID,
        status = ExchangeOrderStatus.PENDING,
        timestamp = System.currentTimeMillis(),
        source = ExchangeSource(
            currency = "USD",
            usdAmount = BigDecimal.TEN,
            currencyAmount = BigDecimal.TEN,
            transactionId = null,
            paymentInstrument = null,
            usdFee = null
        ),
        destination = ExchangeSource(
            currency = "BSV",
            usdAmount = null,
            currencyAmount = BigDecimal.ONE,
            transactionId = CURRENT_TRANSACTION_ID,
            paymentInstrument = null,
            usdFee = null
        ),
        rate = null,
        type = null,
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(
            BuyDetailsFragmentArgs(CURRENT_EXCHANGE_ID, CURRENT_FLOW).toBundle().toMap()
        )

        viewModel = BuyDetailsViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(BuyDetailsContract.State.Loading, actual)
    }

    @Test
    fun onBackClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackClicked() },
            targetEffect = BuyDetailsContract.Effect.Dismiss
        )
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = BuyDetailsContract.Effect.Dismiss
        )
    }

    @Test
    fun onOrderIdClicked_verifyEffect() {
        viewModel.setState {
            BuyDetailsContract.State.Loaded(
                exchangeOrder, CURRENT_FLOW
            )
        }

        viewModel.testEffect(
            action = { viewModel.onOrderIdClicked() },
            targetEffect = BuyDetailsContract.Effect.CopyToClipboard(CURRENT_EXCHANGE_ID)
        )
    }

    @Test
    fun onTransactionIdClicked_verifyEffect() {
        viewModel.setState {
            BuyDetailsContract.State.Loaded(
                exchangeOrder, CURRENT_FLOW
            )
        }

        viewModel.testEffect(
            action = { viewModel.onTransactionIdClicked() },
            targetEffect = BuyDetailsContract.Effect.CopyToClipboard(CURRENT_TRANSACTION_ID)
        )
    }

    companion object {
        private val CURRENT_FLOW = BuyDetailsFlow.PURCHASE
        private const val CURRENT_EXCHANGE_ID = "text_exchange_id"
        private const val CURRENT_TRANSACTION_ID = "text_transaction_id"
    }
}