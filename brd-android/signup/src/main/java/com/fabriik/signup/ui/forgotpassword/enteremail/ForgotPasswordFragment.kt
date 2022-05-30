package com.fabriik.signup.ui.forgotpassword.enteremail

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
import com.fabriik.signup.databinding.FragmentForgotPasswordBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.signup.utils.SnackBarUtils
import com.fabriik.signup.utils.hideKeyboard
import com.fabriik.signup.utils.setValidationState
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(),
    FabriikView<ForgotPasswordContract.State, ForgotPasswordContract.Effect> {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by lazy {
        ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordBinding.bind(view)

        with(binding) {

            // setup input field
            etEmail.doAfterTextChanged {
                viewModel.setEvent(
                    ForgotPasswordContract.Event.EmailChanged(
                        it.toString()
                    )
                )
            }

            // setup "Confirm" button
            btnConfirm.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    ForgotPasswordContract.Event.ConfirmClicked
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

    override fun render(state: ForgotPasswordContract.State) {
        binding.etEmail.setValidationState(state.emailValid)
    }

    override fun handleEffect(effect: ForgotPasswordContract.Effect) {
        when (effect) {
            is ForgotPasswordContract.Effect.GoToResetPassword -> {
                findNavController().navigate(
                    ForgotPasswordFragmentDirections.actionResetPassword()
                )
            }
            is ForgotPasswordContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }
            is ForgotPasswordContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }
        }
    }
}