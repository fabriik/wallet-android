package com.breadwallet.ui.profile

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.settings.SettingsSection
import com.fabriik.kyc.data.enums.AccountVerificationStatus
import dev.zacsweers.redacted.annotations.Redacted

object ProfileScreen {

    data class M(
        val isLoading: Boolean = false,
        val profileData: ProfileData? = null,
        @Redacted val items: List<ProfileItem> = listOf()
    ) {
        companion object {
            fun createDefault() = M()
        }
    }

    sealed class E {
        object OnCloseClicked : E()
        object OnChangeEmailClicked : E()
        object OnVerifyProfileClicked : E()
        object OnUpgradeLimitsClicked : E()
        object OnVerificationMoreInfoClicked : E()
        object OnVerificationDeclinedInfoClicked : E()
        data class OnOptionClicked(val option: ProfileOption) : E()
        data class OnOptionsLoaded(@Redacted val options: List<ProfileItem>) : E()
        data class OnProfileDataLoaded(val data: ProfileData) : E()
    }

    sealed class F {
        object LoadOptions : F()
        object LoadProfileData : F()

        object GoBack : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }

        object GoToKyc : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToKYC
        }

        data class GoToSettings(val section: SettingsSection) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Menu(section)
        }
    }

    data class ProfileData(
        val email: String,
        val verificationStatus: AccountVerificationStatus = AccountVerificationStatus.DEFAULT
    )
}
