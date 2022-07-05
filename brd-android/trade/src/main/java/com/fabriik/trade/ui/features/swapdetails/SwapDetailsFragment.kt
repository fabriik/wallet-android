package com.fabriik.trade.ui.features.swapdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.viewScope
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapDetailsBinding
import kotlinx.coroutines.flow.collect
import com.fabriik.trade.ui.features.swapdetails.SwapStatus.PENDING
import com.fabriik.trade.ui.features.swapdetails.SwapStatus.COMPLETE
import com.fabriik.trade.ui.features.swapdetails.SwapStatus.FAILED

class SwapDetailsFragment : Fragment(),
    FabriikView<SwapDetailsContract.State, SwapDetailsContract.Effect> {

    private lateinit var binding: FragmentSwapDetailsBinding
    private val viewModel: SwapDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swap_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapDetailsBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.DismissClicked)
            }
            tvOrderId.setOnClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.OrderIdClicked)
            }
            tvSwapFromId.setOnClickListener {
                viewModel.setEvent(SwapDetailsContract.Event.TransactionIdClicked)
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

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }

    override fun render(state: SwapDetailsContract.State) {
        with(binding) {
            tvOrderId.text = state.orderId
            tvSwapFromId.text = state.swapFromID
            tvSwapToId.text = state.swapToId
            tvSwapFrom.text = getString(R.string.Swap_Details_From, state.swapFromCurrency)
            tvSwapTo.text = getString(R.string.Swap_Details_To, state.swapToCurrency)
            tvFromCurrencyValue.text = state.swapFromCurrencyValue
            tvToCurrencyValue.text = state.swapToCurrencyValue
            tvSwapFromIdTitle.text =
                getString(
                    R.string.Swap_Details_TransactionIdFrom_Title,
                    state.swapFromCurrency
                )
            tvSwapToIdTitle.text =
                getString(R.string.Swap_Details_TransactionIdTo_Title, state.swapToCurrency)

            icSwapFrom.loadIcon(
                scope = tvSwapFrom.viewScope,
                currencyCode = state.swapFromCurrency
            )

            icSwapTo.loadIcon(
                scope = tvSwapTo.viewScope,
                currencyCode = state.swapToCurrency
            )
            tvTimestamp.text = state.timestamp

            icStatus.setImageResource(setStatusIcon(state.status))
            tvStatus.text = getString(setStatusTitle(state.status))
        }
    }

    override fun handleEffect(effect: SwapDetailsContract.Effect) {
        when (effect) {
            SwapDetailsContract.Effect.Dismiss -> TODO()
            SwapDetailsContract.Effect.CopyOrderId -> TODO()
            SwapDetailsContract.Effect.CopyTransactionId -> TODO()
        }
    }

    private fun setStatusIcon(status: SwapStatus): Int {
        return when (status) {
            PENDING -> R.drawable.ic_status_pending
            COMPLETE -> R.drawable.ic_status_complete
            FAILED -> R.drawable.ic_status_failed
        }
    }

    private fun setStatusTitle(status: SwapStatus): Int {
        return when (status) {
            PENDING -> R.string.Swap_Details_Status_Pending
            COMPLETE -> R.string.Swap_Details_Status_Complete
            FAILED -> R.string.Swap_Details_Status_Failed
        }
    }
}