package com.breadwallet.ui.resetpin.completed

import android.os.Bundle
import android.view.View
import com.breadwallet.databinding.ControllerPinResetCompletedBinding
import com.breadwallet.ui.BaseController
import com.breadwallet.ui.navigation.RouterNavigator

class PinResetCompletedController(args: Bundle? = null) : BaseController(args) {

    private val navigator = RouterNavigator { router }
    private val binding by viewBinding(ControllerPinResetCompletedBinding::inflate)

    override fun onCreateView(view: View) {
        super.onCreateView(view)

        binding.btnToHome.setOnClickListener {
            navigator.home()
        }
    }

    override fun handleBack() = false
}