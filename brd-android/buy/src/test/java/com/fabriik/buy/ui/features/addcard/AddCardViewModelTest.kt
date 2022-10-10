package com.fabriik.buy.ui.features.addcard

import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.processing.testEffect
import com.fabriik.buy.ui.features.processing.toMap
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestFabriikApplication::class)
class AddCardViewModelTest {

    private lateinit var viewModel: AddCardViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(
            AddCardFragmentArgs(CURRENT_FLOW).toBundle().toMap()
        )

        viewModel = AddCardViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertFalse(actual.confirmButtonEnabled)
        Assert.assertFalse(actual.loadingIndicatorVisible)
        Assert.assertEquals("", actual.cardNumber)
        Assert.assertEquals("", actual.expiryDate)
        Assert.assertEquals("", actual.securityCode)
    }

    @Test
    fun onBackClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackClicked() },
            targetEffect = AddCardContract.Effect.Back
        )
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = AddCardContract.Effect.Dismiss
        )
    }

    @Test
    fun onSecurityCodeInfoClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onSecurityCodeInfoClicked() },
            targetEffect = AddCardContract.Effect.ShowCvvInfoDialog
        )
    }

    @Test
    fun onCardNumberChanged_verifyState() {
        val newCardNumber = "1234567890123456"
        viewModel.onCardNumberChanged(newCardNumber)
        Assert.assertEquals("1234 5678 9012 3456", viewModel.currentState.cardNumber)
    }

    @Test
    fun onSecurityCodeChanged_verifyState() {
        val newSecurityCode = "1234"
        viewModel.onSecurityCodeChanged(newSecurityCode)
        Assert.assertEquals(newSecurityCode, viewModel.currentState.securityCode)
    }

    companion object {
        private val CURRENT_FLOW = AddCardFlow.BUY
    }
}