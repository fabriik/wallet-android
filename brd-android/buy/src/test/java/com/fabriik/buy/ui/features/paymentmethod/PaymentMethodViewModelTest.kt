package com.fabriik.buy.ui.features.paymentmethod

import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.buy.ui.features.processing.testEffect
import com.fabriik.buy.ui.features.processing.toMap
import com.fabriik.common.data.model.PaymentInstrument
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestFabriikApplication::class)
class PaymentMethodViewModelTest {

    @Mock lateinit var paymentInstrument: PaymentInstrument

    private lateinit var viewModel: PaymentMethodViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(
            PaymentMethodFragmentArgs(CURRENT_FLOW).toBundle().toMap()
        )

        viewModel = PaymentMethodViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()

        Assert.assertTrue(actual.showDismissButton)
        Assert.assertTrue(actual.paymentInstruments.isEmpty())
        Assert.assertFalse(actual.dataUpdated)
        Assert.assertFalse(actual.initialLoadingIndicator)
        Assert.assertFalse(actual.fullScreenLoadingIndicator)
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = PaymentMethodContract.Effect.Dismiss
        )
    }

    @Test
    fun onAddCardClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onAddCardClicked() },
            targetEffect = PaymentMethodContract.Effect.AddCard(CURRENT_FLOW)
        )
    }

    @Test
    fun onPaymentInstrumentClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onPaymentInstrumentClicked(paymentInstrument) },
            targetEffect = PaymentMethodContract.Effect.Back(PaymentMethodFragment.Result.Selected(paymentInstrument))
        )
    }

    @Test
    fun onPaymentInstrumentOptionsClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onPaymentInstrumentOptionsClicked(paymentInstrument) },
            targetEffect = PaymentMethodContract.Effect.ShowOptionsBottomSheet(paymentInstrument)
        )
    }

    companion object {
        private val CURRENT_FLOW = AddCardFlow.BUY
    }
}