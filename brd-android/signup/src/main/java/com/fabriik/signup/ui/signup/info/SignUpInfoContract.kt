package com.fabriik.signup.ui.signup.info

import com.fabriik.common.ui.base.FabriikContract

interface SignUpInfoContract {

    sealed class Event : FabriikContract.Event {
        object UserAgreementClicked : Event()
        object PrivacyPolicyClicked : Event()
        object SubmitClicked : Event()
        class EmailChanged(val email: String) : Event()
        class PhoneChanged(val phone: String) : Event()
        class PasswordChanged(val password: String) : Event()
        class LastNameChanged(val lastName: String) : Event()
        class FirstNameChanged(val firstName: String) : Event()
        class TermsChanged(val checked: Boolean) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        class GoToConfirmation(val sessionKey: String) : Effect()
        class OpenWebsite(val url: String) : Effect()
        class ShowLoading(val show: Boolean) : Effect()
        class ShowSnackBar(val message: String) : Effect()
    }

    data class State(
        val email: String = "",
        val emailValid: Boolean = false,
        val phone: String = "",
        val phoneValid: Boolean = false,
        val password: String = "",
        val passwordValid: Boolean = false,
        val lastName: String = "",
        val lastNameValid: Boolean = false,
        val firstName: String = "",
        val firstNameValid: Boolean = false,
        val termsAccepted: Boolean = false
    ) : FabriikContract.State
}