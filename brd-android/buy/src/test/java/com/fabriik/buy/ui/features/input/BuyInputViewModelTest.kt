package com.fabriik.buy.ui.features.input

import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.processing.testEffect
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
class BuyInputViewModelTest {

    private lateinit var viewModel: BuyInputViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = BuyInputViewModel(RuntimeEnvironment.application)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(BuyInputContract.State.Loading, actual)
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = BuyInputContract.Effect.Dismiss
        )
    }
}