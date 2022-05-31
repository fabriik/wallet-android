package com.fabriik.signup.ui.signup.confirmemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.signup.R
import com.fabriik.signup.databinding.FragmentSignUpConfirmEmailBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.SnackBarUtils
import com.fabriik.signup.utils.hideKeyboard
import com.fabriik.signup.utils.setValidationState
import kotlinx.coroutines.flow.collect

class SignUpConfirmEmailFragment : Fragment(),
    FabriikView<SignUpConfirmEmailContract.State, SignUpConfirmEmailContract.Effect> {

    private lateinit var binding: FragmentSignUpConfirmEmailBinding
    private val viewModel: SignUpConfirmEmailViewModel by lazy {
        ViewModelProvider(this).get(SignUpConfirmEmailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sign_up_confirm_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpConfirmEmailBinding.bind(view)

        with(binding) {

            // setup "Confirmation code" input field
            etCode.doAfterTextChanged {
                viewModel.setEvent(
                    SignUpConfirmEmailContract.Event.ConfirmationCodeChanged(
                        it.toString()
                    )
                )
            }

            // setup "Confirm" button
            btnConfirm.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    SignUpConfirmEmailContract.Event.ConfirmClicked
                )
            }

            // setup "Resend code" button
            tvResend.setOnClickListener {
                viewModel.setEvent(
                    SignUpConfirmEmailContract.Event.ResendCodeClicked
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

    override fun render(state: SignUpConfirmEmailContract.State) {
        binding.etCode.setValidationState(state.confirmationCodeValid)
    }

    override fun handleEffect(effect: SignUpConfirmEmailContract.Effect) {
        when (effect) {
            is SignUpConfirmEmailContract.Effect.GoToLogin -> {
                findNavController().navigate(
                    SignUpConfirmEmailFragmentDirections.actionLogIn()
                )
            }
            is SignUpConfirmEmailContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }
            is SignUpConfirmEmailContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }
        }
    }
}