package com.fabriik.signup.ui.login

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
import com.fabriik.signup.databinding.FragmentLogInBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.*
import com.fabriik.signup.utils.clickableSpan
import com.fabriik.signup.utils.underline
import kotlinx.coroutines.flow.collect

class LogInFragment : Fragment(), FabriikView<LogInContract.State, LogInContract.Effect> {

    private lateinit var binding: FragmentLogInBinding
    private val viewModel: LogInViewModel by lazy {
        ViewModelProvider(this).get(LogInViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLogInBinding.bind(view)

        with(binding) {
            // setup "Forgot password?" button
            tvForgotPassword.underline()
            tvForgotPassword.setOnClickListener {
                viewModel.setEvent(
                    LogInContract.Event.ForgotPasswordClicked
                )
            }

            // setup "Don't have an account" view
            tvNoAccount.clickableSpan(
                fullTextRes = R.string.LogIn_NoAccount,
                clickableParts = mapOf(
                    R.string.LogIn_SignUp to {
                        viewModel.setEvent(
                            LogInContract.Event.SignUpClicked
                        )
                    }
                )
            )

            // setup "Submit" button
            btnSubmit.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    LogInContract.Event.SubmitClicked
                )
            }

            // setup Email input field
            etEmail.doAfterTextChanged {
                viewModel.setEvent(
                    LogInContract.Event.EmailChanged(
                        it.toString()
                    )
                )
            }

            // setup Password input field
            etPassword.doAfterTextChanged {
                viewModel.setEvent(
                    LogInContract.Event.PasswordChanged(
                        it.toString()
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

    override fun render(state: LogInContract.State) {
        with(binding) {
            etEmail.setValidationState(state.emailValid)
            etPassword.setValidationState(state.passwordValid)
        }
    }

    override fun handleEffect(effect: LogInContract.Effect) {
        when (effect) {
            LogInContract.Effect.GoToKyc -> {
                findNavController().navigate(
                    LogInFragmentDirections.actionKyc()
                )
            }
            LogInContract.Effect.GoToSignUp -> {
                findNavController().navigate(
                    LogInFragmentDirections.actionSignUp()
                )
            }
            LogInContract.Effect.GoToForgotPassword -> {
                findNavController().navigate(
                    LogInFragmentDirections.actionForgotPassword()
                )
            }
            is LogInContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }
            is LogInContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }
        }
    }
}