package com.fabriik.trade.ui.features.authentication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.breadwallet.legacy.presenter.customviews.PinLayout
import com.breadwallet.tools.animation.SpringAnimator
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapAuthenticationBinding
import kotlinx.coroutines.flow.collect

class SwapAuthenticationFragment : Fragment(),
    FabriikView<SwapAuthenticationContract.State, SwapAuthenticationContract.Effect> {

    private lateinit var binding: FragmentSwapAuthenticationBinding
    private val viewModel: SwapAuthenticationViewModel by viewModels()

    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_swap_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapAuthenticationBinding.bind(view)

        with (binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(SwapAuthenticationContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SwapAuthenticationContract.Event.DismissClicked)
            }

            pinLayout.setup(keyboard, false,  object: PinLayout.PinLayoutListener {
                override fun onValidPinInserted(pin: String) {
                    viewModel.setEvent(SwapAuthenticationContract.Event.PinValidated(true))
                }

                override fun onInvalidPinInserted(pin: String, attemptsLeft: Int) {
                    binding.pinLayout.resetPin()
                    viewModel.setEvent(SwapAuthenticationContract.Event.PinValidated(false))
                }

                override fun onPinLocked() {
                    // empty
                }
            })
        }

        biometricPrompt = BiometricPrompt(
            requireActivity(),
            ContextCompat.getMainExecutor(requireActivity()),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.setEvent(SwapAuthenticationContract.Event.AuthSucceeded)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    viewModel.setEvent(SwapAuthenticationContract.Event.AuthFailed(errorCode))
                }

                // Ignored: only handle final events from onAuthenticationError
                override fun onAuthenticationFailed() = Unit
            }
        )

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        when (viewModel.state.value.authMode) {
            SwapAuthenticationContract.AuthMode.PIN_REQUIRED -> Unit //nothing to do
            SwapAuthenticationContract.AuthMode.BIOMETRIC_REQUIRED, SwapAuthenticationContract.AuthMode.USER_PREFERRED -> {
                biometricPrompt.authenticate(
                    BiometricPrompt.PromptInfo.Builder()
                        .setTitle(resources.getString(R.string.UnlockScreen_touchIdTitle_android))
                        .setNegativeButtonText(resources.getString(R.string.Prompts_TouchId_usePin_android))
                        .build()
                )
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        binding.pinLayout.cleanUp()
    }

    override fun render(state: SwapAuthenticationContract.State) {
        binding.root.isVisible =
            state.authMode == SwapAuthenticationContract.AuthMode.PIN_REQUIRED
    }

    override fun handleEffect(effect: SwapAuthenticationContract.Effect) {
        when (effect) {
            SwapAuthenticationContract.Effect.Dismiss ->
                requireActivity().finish()

            SwapAuthenticationContract.Effect.ShakeError -> {
                binding.pinLayout.resetPin()
                SpringAnimator.failShakeAnimation(requireContext(), binding.pinLayout)
            }

            is SwapAuthenticationContract.Effect.Back -> {
                parentFragmentManager.setFragmentResult(
                    SwapAuthenticationViewModel.REQUEST_KEY, bundleOf(
                        SwapAuthenticationViewModel.EXTRA_RESULT to effect.resultKey
                    )
                )
                findNavController().popBackStack()
            }
        }
    }
}