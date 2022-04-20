package com.fabriik.signup.ui.forgotpassword.enteremail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.signup.R
import com.fabriik.signup.databinding.FragmentForgotPasswordEnterEmailBinding
import com.fabriik.signup.databinding.FragmentSignUpConfirmEmailBinding
import com.fabriik.signup.ui.SignupActivity
import com.fabriik.signup.ui.base.FabriikView
import com.fabriik.signup.utils.SnackBarUtils
import com.fabriik.signup.utils.hideKeyboard
import com.fabriik.signup.utils.setValidator
import com.fabriik.signup.utils.validators.ConfirmationCodeValidator
import com.fabriik.signup.utils.validators.EmailValidator
import kotlinx.coroutines.flow.collect

class ForgotPasswordEnterEmailFragment : Fragment(),
    FabriikView<ForgotPasswordEnterEmailContract.State, ForgotPasswordEnterEmailContract.Effect> {

    private lateinit var binding: FragmentForgotPasswordEnterEmailBinding
    private val viewModel: ForgotPasswordEnterEmailViewModel by lazy {
        ViewModelProvider(this).get(ForgotPasswordEnterEmailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forgot_password_enter_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordEnterEmailBinding.bind(view)

        with(binding) {

            // setup input field
            etEmail.setValidator(EmailValidator)

            // setup "Confirm" button
            btnConfirm.setOnClickListener {
                hideKeyboard()

                viewModel.setEvent(
                    ForgotPasswordEnterEmailContract.Event.ConfirmClicked(
                        binding.etEmail.text.toString()
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

    override fun render(state: ForgotPasswordEnterEmailContract.State) {
        //empty
    }

    override fun handleEffect(effect: ForgotPasswordEnterEmailContract.Effect) {
        when (effect) {
            is ForgotPasswordEnterEmailContract.Effect.GoToResetPassword -> {
                /*findNavController().navigate(
                    SignUpConfirmEmailFragmentDirections.actionLogIn()
                )*/ //todo
            }
            is ForgotPasswordEnterEmailContract.Effect.ShowLoading -> {
                val activity = activity as SignupActivity?
                activity?.showLoading(effect.show)
            }
            is ForgotPasswordEnterEmailContract.Effect.ShowSnackBar -> {
                SnackBarUtils.showLong(
                    view = binding.root,
                    text = effect.message
                )
            }
        }
    }
}