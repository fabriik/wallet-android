package com.fabriik.registration.ui.features.verifyemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.textOrEmpty
import com.fabriik.registration.R
import com.fabriik.registration.databinding.FragmentRegistrationVerifyEmailBinding
import kotlinx.coroutines.flow.collect

class RegistrationVerifyEmailFragment : Fragment(),
    FabriikView<RegistrationVerifyEmailContract.State, RegistrationVerifyEmailContract.Effect> {

    private lateinit var binding: FragmentRegistrationVerifyEmailBinding
    private val viewModel: RegistrationVerifyEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registration_verify_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegistrationVerifyEmailBinding.bind(view)

        with(binding) {
            btnConfirm.setOnClickListener {
                viewModel.setEvent(RegistrationVerifyEmailContract.Event.ConfirmClicked)
            }

            btnResend.setOnClickListener {
                viewModel.setEvent(RegistrationVerifyEmailContract.Event.ResendEmailClicked)
            }

            btnChangeEmail.setOnClickListener {
                viewModel.setEvent(RegistrationVerifyEmailContract.Event.ChangeEmailClicked)
            }

            btnDismiss.setOnClickListener {
                viewModel.setEvent(RegistrationVerifyEmailContract.Event.DismissClicked)
            }

            viewEnterCode.doAfterTextChanged {
                viewModel.setEvent(
                    RegistrationVerifyEmailContract.Event.CodeChanged(
                        it.textOrEmpty()
                    )
                )
            }
        }

        // collect UI state
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                render(it)
            }
        }

        // collect UI effect
        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                handleEffect(it)
            }
        }
    }

    override fun render(state: RegistrationVerifyEmailContract.State) {
        with (binding) {
            tvSubtitle.text = state.subtitle
            btnConfirm.isEnabled = state.confirmEnabled
            viewOverlay.isVisible = state.verifiedOverlayVisible
            ivEmailVerified.isVisible = state.verifiedOverlayVisible
        }
    }

    override fun handleEffect(effect: RegistrationVerifyEmailContract.Effect) {
        when (effect) {
            is RegistrationVerifyEmailContract.Effect.Dismiss ->
                requireActivity().finish()
        }
    }
}