package com.fabriik.buy.ui.features.paymentmethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentPaymentMethodBinding
import com.fabriik.common.ui.base.FabriikView

class PaymentMethodFragment : Fragment(),
    FabriikView<PaymentMethodContract.State, PaymentMethodContract.Effect> {

    lateinit var binding: FragmentPaymentMethodBinding

    private val viewModel: PaymentMethodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentMethodBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.OnBackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.OnDismissClicked)
            }
        }
    }

    override fun render(state: PaymentMethodContract.State) {
        with(binding) {

        }
    }

    override fun handleEffect(effect: PaymentMethodContract.Effect) {
        when (effect) {
            PaymentMethodContract.Effect.Back ->
                findNavController().popBackStack()

            PaymentMethodContract.Effect.Dismiss ->
                activity?.finish()
        }
    }
}