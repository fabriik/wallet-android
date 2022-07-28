package com.fabriik.buy.ui.features.processing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentPaymentProcessingBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class PaymentProcessingFragment : Fragment(),
    FabriikView<PaymentProcessingContract.State, PaymentProcessingContract.Effect> {

    private lateinit var binding: FragmentPaymentProcessingBinding
    private val viewModel: PaymentProcessingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment_processing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentProcessingBinding.bind(view)

        with(binding) {
            btnHome.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.BackToHomeClicked)
            }

            btnContactSupport.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.ContactSupportClicked)
            }

            btnPurchaseDetails.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.PurchaseDetailsClicked)
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

    override fun render(state: PaymentProcessingContract.State) {
        when (state) {
            is PaymentProcessingContract.State.Processing ->
                renderProcessingState()

            is PaymentProcessingContract.State.Loaded ->
                renderLoadedState(state)
        }
    }

    override fun handleEffect(effect: PaymentProcessingContract.Effect) {
        when (effect) {
            PaymentProcessingContract.Effect.Dismiss ->
                activity?.finish()

            PaymentProcessingContract.Effect.ContactSupport ->
                TODO()

            is PaymentProcessingContract.Effect.GoToPurchaseDetails ->
                TODO()
        }
    }

    private fun renderProcessingState() {
        with(binding) {
            contentLoaded.isVisible = false
            contentProcessing.isVisible = true
        }
    }

    private fun renderLoadedState(state: PaymentProcessingContract.State.Loaded) {
        with(binding) {
            contentLoaded.isVisible = true
            contentProcessing.isVisible = false

            ivIcon.setImageResource(state.status.icon)
            tvTitle.setText(state.status.title)
            tvDescription.setText(state.status.description)
            btnContactSupport.isVisible = state.status.contactSupportVisible
            btnPurchaseDetails.isVisible = state.status.purchaseDetailsVisible
        }
    }
}