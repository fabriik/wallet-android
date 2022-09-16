package com.fabriik.kyc.ui.features.personalinformation

import com.fabriik.kyc.data.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PersonalInformationEventHandlerTest {

    @Mock lateinit var country: Country

    @Spy val handler = object : PersonalInformationEventHandler {
        override fun onBackClicked() {}
        override fun onDateClicked() {}
        override fun onCountryClicked() {}
        override fun onConfirmClicked() {}
        override fun onDismissClicked() {}
        override fun onLoadProfile() {}
        override fun onNameChanged(name: String) {}
        override fun onDateChanged(date: Long) {}
        override fun onLastNameChanged(lastName: String) {}
        override fun onCountryChanged(country: Country) {}
    }

    @Test
    fun handleEvent_loadProfile_callOnLoadProfile() {
        handler.handleEvent(PersonalInformationContract.Event.LoadProfile)
        verify(handler).onLoadProfile()
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(PersonalInformationContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dateClicked_callOnDateClicked() {
        handler.handleEvent(PersonalInformationContract.Event.DateClicked)
        verify(handler).onDateClicked()
    }

    @Test
    fun handleEvent_countryClicked_callOnCountryClicked() {
        handler.handleEvent(PersonalInformationContract.Event.CountryClicked)
        verify(handler).onCountryClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(PersonalInformationContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(PersonalInformationContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_nameChanged_callOnNameChanged() {
        val name = "test_name"
        handler.handleEvent(PersonalInformationContract.Event.NameChanged(name))
        verify(handler).onNameChanged(name)
    }

    @Test
    fun handleEvent_lastNameChanged_callOnLastNameChanged() {
        val name = "test_name"
        handler.handleEvent(PersonalInformationContract.Event.LastNameChanged(name))
        verify(handler).onLastNameChanged(name)
    }

    @Test
    fun handleEvent_dateChanged_callOnDateChanged() {
        val timestamp = System.currentTimeMillis()
        handler.handleEvent(PersonalInformationContract.Event.DateChanged(timestamp))
        verify(handler).onDateChanged(timestamp)
    }

    @Test
    fun handleEvent_countryChanged_callOnCountryChanged() {
        handler.handleEvent(PersonalInformationContract.Event.CountryChanged(country))
        verify(handler).onCountryChanged(country)
    }
}