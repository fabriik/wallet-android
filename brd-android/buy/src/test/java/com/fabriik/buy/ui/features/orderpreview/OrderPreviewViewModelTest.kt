package com.fabriik.buy.ui.features.orderpreview

import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.R
import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.processing.testEffect
import com.fabriik.buy.ui.features.processing.toMap
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
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
class OrderPreviewViewModelTest {

    @Mock lateinit var networkFee: FeeAmountData
    @Mock lateinit var quoteResponse: QuoteResponse
    @Mock lateinit var paymentInstrument: PaymentInstrument

    private lateinit var viewModel: OrderPreviewViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        savedStateHandle = SavedStateHandle(
            OrderPreviewFragmentArgs(
                TEST_FIAT_AMOUNT, TEST_FIAT_CURRENCY, TEST_CRYPTO_CURRENCY,
                networkFee, quoteResponse, paymentInstrument
            ).toBundle().toMap()
        )

        viewModel = OrderPreviewViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()

        Assert.assertEquals(TEST_FIAT_AMOUNT, actual.fiatAmount)
        Assert.assertEquals(TEST_FIAT_CURRENCY, actual.fiatCurrency)
        Assert.assertEquals(TEST_CRYPTO_CURRENCY, actual.cryptoCurrency)
        Assert.assertEquals(networkFee, actual.networkFee)
        Assert.assertEquals(quoteResponse, actual.quoteResponse)
        Assert.assertEquals(paymentInstrument, actual.paymentInstrument)

        Assert.assertEquals("", actual.securityCode)

        Assert.assertFalse(actual.confirmButtonEnabled)
        Assert.assertFalse(actual.fullScreenLoadingIndicator)
    }

    @Test
    fun onBackClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackClicked() },
            targetEffect = OrderPreviewContract.Effect.Back
        )
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = OrderPreviewContract.Effect.Dismiss
        )
    }

    @Test
    fun onConfirmClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onConfirmClicked() },
            targetEffect = OrderPreviewContract.Effect.RequestUserAuthentication
        )
    }

    @Test
    fun onCreditInfoClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onCreditInfoClicked() },
            targetEffect = OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Swap_CardFee,
                description = R.string.Buy_CardFee
            )
        )
    }

    @Test
    fun onNetworkInfoClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onNetworkInfoClicked() },
            targetEffect = OrderPreviewContract.Effect.ShowInfoDialog(
                title = R.string.Buy_NetworkFees,
                description = R.string.Buy_NetworkFeeMessage
            )
        )
    }

    @Test
    fun onSecurityCodeInfoClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onSecurityCodeInfoClicked() },
            targetEffect = OrderPreviewContract.Effect.ShowInfoDialog(
                image = R.drawable.ic_info_cvv,
                title = R.string.Buy_SecurityCode,
                description = R.string.Buy_SecurityCodePopup
            )
        )
    }

    @Test
    fun onTermsAndConditionsClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onTermsAndConditionsClicked() },
            targetEffect = OrderPreviewContract.Effect.OpenWebsite(
                FabriikApiConstants.URL_TERMS_AND_CONDITIONS
            )
        )
    }

    @Test
    fun onSecurityCodeChanged_verifyState() {
        val newSecurityCode = "123"
        viewModel.onSecurityCodeChanged(newSecurityCode)
        Assert.assertEquals(newSecurityCode, viewModel.currentState.securityCode)
    }

    private companion object {
        private val TEST_FIAT_AMOUNT = BigDecimal.TEN
        private const val TEST_FIAT_CURRENCY = "test_fiat_currency"
        private const val TEST_CRYPTO_CURRENCY = "test_crypto_currency"
    }
}