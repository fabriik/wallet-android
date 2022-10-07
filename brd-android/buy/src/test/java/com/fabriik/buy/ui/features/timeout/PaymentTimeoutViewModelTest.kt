package com.fabriik.buy.ui.features.timeout

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentTimeoutViewModelTest {

    @get:Rule
    val rule = TestCoroutineRule()

    @Mock
    lateinit var application: Application

    private lateinit var viewModel: PaymentTimeoutViewModel

    @Before
    fun setUp() {
        viewModel = Mockito.spy(PaymentTimeoutViewModel(application))
    }

    @Test
    fun createInitialState_verifyInitialState() {
        val actual = viewModel.createInitialState()
        Assert.assertEquals(PaymentTimeoutContract.State, actual)
    }

    @Test
    fun onTryAgainClicked_verifyEffect() = rule.testDispatcher.runBlockingTest {
        val actual = viewModel.effect
            .onStart { viewModel.onTryAgainClicked() }
            .first()

        Assert.assertEquals(PaymentTimeoutContract.Effect.BackToBuy, actual)
    }
}

class TestCoroutineRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
