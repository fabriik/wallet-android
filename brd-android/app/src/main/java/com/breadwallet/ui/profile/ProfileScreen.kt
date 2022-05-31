package com.breadwallet.ui.profile

import com.breadwallet.ui.navigation.NavigationEffect
import com.breadwallet.ui.navigation.NavigationTarget
import com.breadwallet.ui.settings.SettingsItem
import com.breadwallet.ui.settings.SettingsOption
import com.breadwallet.ui.settings.SettingsSection
import dev.zacsweers.redacted.annotations.Redacted

object ProfileScreen {

    data class M(
        @Redacted val items: List<SettingsItem> = listOf(),
        val isLoading: Boolean = false
    ) {
        companion object {
            fun createDefault() = M()
        }
    }

    sealed class E {
        object OnCloseClicked : E()
        data class OnOptionClicked(val option: SettingsOption) : E()
        data class OnOptionsLoaded(@Redacted val options: List<SettingsItem>) : E()
    }

    sealed class F {
        object LoadOptions : F()

        object GoBack : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Back
        }

        data class GoToSettings(val section: SettingsSection) : F(), NavigationEffect {
            override val navigationTarget = NavigationTarget.Menu(section)
        }
    }
}
