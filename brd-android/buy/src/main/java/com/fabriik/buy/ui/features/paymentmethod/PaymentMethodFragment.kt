package com.fabriik.buy.ui.features.paymentmethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentPaymentMethodBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class PaymentMethodFragment : Fragment(),
    FabriikView<PaymentMethodContract.State, PaymentMethodContract.Effect> {

    private lateinit var binding: FragmentPaymentMethodBinding
    private val viewModel: PaymentMethodViewModel by viewModels()

    private val adapter = PaymentMethodSelectionAdapter {
        viewModel.setEvent(
            PaymentMethodContract.Event.PaymentInstrumentSelected(it)
        )
    }

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
                viewModel.setEvent(PaymentMethodContract.Event.BackClicked)
            }

            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.DismissClicked)
            }

            cvAddCard.setOnClickListener {
                viewModel.setEvent(PaymentMethodContract.Event.AddCardClicked)
            }

            val layoutManager = LinearLayoutManager(context)
            rvListCards.adapter = adapter
            rvListCards.layoutManager = layoutManager
            rvListCards.setHasFixedSize(true)
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

    override fun render(state: PaymentMethodContract.State) {
        adapter.submitList(state.paymentInstruments)
    }

    override fun handleEffect(effect: PaymentMethodContract.Effect) {
        when (effect) {
            is PaymentMethodContract.Effect.Back -> {
                parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to effect.selectedInstrument))
                findNavController().popBackStack()
            }

            PaymentMethodContract.Effect.Dismiss ->
                activity?.finish()

            PaymentMethodContract.Effect.AddCard ->
                findNavController().navigate(PaymentMethodFragmentDirections.actionAddCard())
        }
    }

    companion object {
        const val REQUEST_KEY = "request_payment_method"
        const val RESULT_KEY = "result_payment_method"
    }
}