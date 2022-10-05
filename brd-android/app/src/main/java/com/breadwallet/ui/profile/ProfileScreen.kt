package com.breadwallet.ui.profile

import com.breadwallet.R
import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.settings.SettingsSection
import com.fabriik.common.data.model.Profile
import dev.zacsweers.redacted.annotations.Redacted

object ProfileScreen {

    data class M(
        val isLoading: Boolean = false,
        val profile: Profile? = null,
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
        data class OnProfileDataLoaded(val profile: Profile) : E()
        data class OnProfileDataLoadFailed(val message: String?) : E()
    }

    sealed class F {
        data class LoadOptions(val profile: Profile?) : F()
        object LoadProfileData : F()

        object GoBack : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }

        object GoToKyc : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.GoToKyc
        }

        data class GoToSettings(val section: SettingsSection) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Menu(section)
        }

        object GoToPaymentMethod : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.PaymentMethod
        }

        object ShowInfoDialog : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.ShowInfoDialog(
                title = R.string.Profile_InfoDialog_title,
                description = R.string.Profile_InfoDialog_description,
            )
        }
        
        data class ShowFabriikToast(val message: String) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.FabriikToast(
                message = message,
                type = NavigationTarget.FabriikToast.Type.INFO
            )
        }
    }
}
