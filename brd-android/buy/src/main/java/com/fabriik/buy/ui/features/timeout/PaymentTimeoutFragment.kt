package com.fabriik.buy.ui.features.timeout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fabriik.buy.R
import com.fabriik.buy.databinding.FragmentPaymentTimeoutBinding
import com.fabriik.common.ui.base.FabriikView
import kotlinx.coroutines.flow.collect

class PaymentTimeoutFragment : Fragment(),
    FabriikView<PaymentTimeoutContract.State, PaymentTimeoutContract.Effect> {

    private lateinit var binding: FragmentPaymentTimeoutBinding
    private val viewModel: PaymentTimeoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment_timeout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPaymentTimeoutBinding.bind(view)

        with(binding) {
            btnTryAgain.setOnClickListener {
                viewModel.setEvent(PaymentTimeoutContract.Event.TryAgainClicked)
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

    override fun render(state: PaymentTimeoutContract.State) {
        // empty
    }

    override fun handleEffect(effect: PaymentTimeoutContract.Effect) {
        when (effect) {
            PaymentTimeoutContract.Effect.BackToBuy -> {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_TRY_AGAIN)
                )

                findNavController().popBackStack(
                    R.id.fragmentBuyInput, false
                )
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "requestPaymentTimeout"
        const val RESULT_KEY = "result"
        const val RESULT_TRY_AGAIN = "resultTryAgain"
    }
}