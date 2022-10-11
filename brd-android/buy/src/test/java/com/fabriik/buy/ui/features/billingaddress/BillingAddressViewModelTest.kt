package com.fabriik.buy.ui.features.billingaddress

import androidx.lifecycle.SavedStateHandle
import com.fabriik.buy.ui.features.TestFabriikApplication
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.buy.ui.features.processing.testEffect
import com.fabriik.buy.ui.features.processing.toMap
import com.fabriik.kyc.data.model.Country
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
class BillingAddressViewModelTest {

    @Mock lateinit var country: Country

    private lateinit var viewModel: BillingAddressViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        savedStateHandle = SavedStateHandle(
            BillingAddressFragmentArgs(CARD_TOKEN, CURRENT_FLOW).toBundle().toMap()
        )

        viewModel = BillingAddressViewModel(RuntimeEnvironment.application, savedStateHandle)
    }

    @Test
    fun createInitialState_verifyData() {
        val actual = viewModel.createInitialState()

        Assert.assertFalse(actual.confirmEnabled)
        Assert.assertFalse(actual.loadingIndicatorVisible)

        Assert.assertEquals("", actual.firstName)
        Assert.assertEquals("", actual.lastName)
        Assert.assertEquals("", actual.zip)
        Assert.assertEquals("", actual.city)
        Assert.assertEquals("", actual.address)

        Assert.assertNull(actual.state)
        Assert.assertNull(actual.country)
        Assert.assertNull(actual.paymentReference)
    }

    @Test
    fun onBackClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onBackClicked() },
            targetEffect = BillingAddressContract.Effect.Back
        )
    }

    @Test
    fun onDismissClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onDismissClicked() },
            targetEffect = BillingAddressContract.Effect.Dismiss
        )
    }

    @Test
    fun onCountryClicked_verifyEffect() {
        viewModel.testEffect(
            action = { viewModel.onCountryClicked() },
            targetEffect = BillingAddressContract.Effect.CountrySelection
        )
    }

    @Test
    fun onZipChanged_verifyState() {
        val newZip = "1234"
        viewModel.onZipChanged(newZip)
        Assert.assertEquals(newZip, viewModel.currentState.zip)
    }

    @Test
    fun onCityChanged_verifyState() {
        val newCity = "Test city"
        viewModel.onCityChanged(newCity)
        Assert.assertEquals(newCity, viewModel.currentState.city)
    }

    @Test
    fun onStateChanged_verifyState() {
        val newState = "Test state"
        viewModel.onStateChanged(newState)
        Assert.assertEquals(newState, viewModel.currentState.state)
    }

    @Test
    fun onAddressChanged_verifyState() {
        val newAddress = "Test address"
        viewModel.onAddressChanged(newAddress)
        Assert.assertEquals(newAddress, viewModel.currentState.address)
    }

    @Test
    fun onCountryChanged_verifyState() {
        viewModel.onCountryChanged(country)
        Assert.assertEquals(country, viewModel.currentState.country)
    }

    @Test
    fun onFirstNameChanged_verifyState() {
        val newFirstName = "Test firstname"
        viewModel.onFirstNameChanged(newFirstName)
        Assert.assertEquals(newFirstName, viewModel.currentState.firstName)
    }

    @Test
    fun onLastNameChanged_verifyState() {
        val newLastName = "Test lastname"
        viewModel.onLastNameChanged(newLastName)
        Assert.assertEquals(newLastName, viewModel.currentState.lastName)
    }

    companion object {
        private val CURRENT_FLOW = AddCardFlow.BUY
        private const val CARD_TOKEN = "token_1234"
    }
}