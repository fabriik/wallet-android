package com.fabriik.buy.ui.features.processing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.data.enums.BuyDetailsFlow
import com.fabriik.buy.databinding.FragmentPaymentProcessingBinding
import com.fabriik.common.data.FabriikApiConstants
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.ui.features.processing.SwapProcessingContract
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

            btnDifferentMethod.setOnClickListener {
                viewModel.setEvent(PaymentProcessingContract.Event.TryDifferentMethodClicked)
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
        with(binding) {
            contentLoaded.isVisible = true
            contentProcessing.isVisible = false

            ivIcon.setImageResource(state.status.icon)
            tvTitle.setText(state.status.title)
            tvDescription.setText(state.status.description)
            btnHome.isVisible = state.status.goHomeVisible
            btnContactSupport.isVisible = state.status.contactSupportVisible
            btnPurchaseDetails.isVisible = state.status.purchaseDetailsVisible
            btnDifferentMethod.isVisible = state.status.tryDifferentMethodVisible
        }
    }

    override fun handleEffect(effect: PaymentProcessingContract.Effect) {
        when (effect) {
            PaymentProcessingContract.Effect.Dismiss ->
                activity?.finish()

            PaymentProcessingContract.Effect.BackToBuy ->
                findNavController().popBackStack(
                    R.id.fragmentBuyInput, false
                )

            PaymentProcessingContract.Effect.ContactSupport -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FabriikApiConstants.URL_SUPPORT_PAGE))
                startActivity(intent)
            }

            is PaymentProcessingContract.Effect.GoToPurchaseDetails ->
                findNavController().navigate(
                    PaymentProcessingFragmentDirections.actionBuyDetails(
                        exchangeId = effect.purchaseId,
                        flow = BuyDetailsFlow.PURCHASE
                    )
                )
        }
    }
}