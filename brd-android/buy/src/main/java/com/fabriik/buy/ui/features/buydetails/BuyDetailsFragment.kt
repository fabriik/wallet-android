package com.fabriik.buy.ui.features.buydetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.breadwallet.tools.manager.BRClipboardManager
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.buy.R
import com.fabriik.trade.data.response.ExchangeOrderStatus
import com.fabriik.buy.databinding.FragmentBuyDetailsBinding
import com.fabriik.common.utils.viewScope
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*

class BuyDetailsFragment : Fragment(),
    FabriikView<BuyDetailsContract.State, BuyDetailsContract.Effect> {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

    private lateinit var binding: FragmentBuyDetailsBinding
    private val viewModel: BuyDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyDetailsBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.DismissClicked)
            }
            tvOrderId.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.OrderIdClicked)
            }
            tvCryptoTransactionId.setOnClickListener {
                viewModel.setEvent(BuyDetailsContract.Event.TransactionIdClicked)
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

        viewModel.setEvent(BuyDetailsContract.Event.LoadData)
    }

    override fun render(state: BuyDetailsContract.State) {
        when (state) {
            is BuyDetailsContract.State.Error ->
                showErrorState()

            is BuyDetailsContract.State.Loading ->
                showLoadingState()

            is BuyDetailsContract.State.Loaded ->
                showLoadedState(state)
        }
    }

    override fun handleEffect(effect: BuyDetailsContract.Effect) {
        when (effect) {
            BuyDetailsContract.Effect.Dismiss ->
                requireActivity().finish()

            is BuyDetailsContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    binding.root, effect.message
                )

            is BuyDetailsContract.Effect.CopyToClipboard ->
                copyToClipboard(effect.data)
        }
    }

    private fun showErrorState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun showLoadingState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun showLoadedState(state: BuyDetailsContract.State.Loaded) {
        val data = state.data

        with(binding) {
            tvOrderId.text = data.orderId

            ivCrypto.loadIcon(
                scope = binding.root.viewScope,
                currencyCode = data.source.currency
            )

            val date = Date(data.timestamp)
            tvTimestamp.text = dateFormatter.format(date)

            icStatus.setImageResource(setStatusIcon(data.status))
            tvStatus.text = getString(setStatusTitle(data.status))

            content.isVisible = true
            initialLoadingIndicator.isVisible = false
        }
            /*binding.root.post {
                tvSwapTo.text = getString(R.string.Swap_Details_To, data.destination.currency.toUpperCase(Locale.getDefault()))
                tvSwapToIdTitle.text = getString(
                    R.string.Swap_Details_TransactionIdTo_Title, data.destination.currency.toUpperCase(Locale.getDefault())
                )

                if (data.destination.transactionId.isNullOrEmpty()) {
                    tvSwapToId.text = getString(R.string.Swap_Details_Status_Pending)
                    tvSwapToId.setCompoundDrawablesRelative(null, null, null, null)
                    tvSwapToId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_text_02))
                } else {
                    tvSwapToId.text = data.destination.transactionId
                    tvSwapToId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_link_02))
                }

                icSwapTo.loadIcon(
                    scope = tvSwapFrom.viewScope,
                    currencyCode = data.destination.currency
                )

                val formatFiatTo = data.destination.usdAmount?.formatFiatForUi("USD") ?: "? USD"
                val formatCryptoTo = data.destination.currencyAmount.formatCryptoForUi(null)
                tvToCurrencyValue.text = "$formatCryptoTo / $formatFiatTo"

                tvSwapFrom.text = getString(R.string.Swap_Details_From, data.source.currency.toUpperCase(Locale.getDefault()))
                tvSwapFromIdTitle.text = getString(
                    R.string.Swap_Details_TransactionIdFrom_Title, data.source.currency.toUpperCase(Locale.getDefault())
                )

                if (data.source.transactionId.isNullOrEmpty()) {
                    tvSwapFromId.text = getString(R.string.Swap_Details_Status_Pending)
                    tvSwapFromId.setCompoundDrawablesRelative(null, null, null, null)
                    tvSwapFromId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_text_02))
                } else {
                    tvSwapFromId.text = data.source.transactionId
                    tvSwapFromId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_link_02))
                }

                val formatFiatFrom = data.source.usdAmount?.formatFiatForUi("USD") ?: "? USD"
                val formatCryptoFrom = data.source.currencyAmount.formatCryptoForUi(null)
                tvFromCurrencyValue.text = "$formatCryptoFrom / $formatFiatFrom"
            }
        }*/
    }

    private fun setStatusIcon(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.drawable.ic_status_pending
            ExchangeOrderStatus.FAILED -> R.drawable.ic_status_failed
            ExchangeOrderStatus.COMPLETE -> R.drawable.ic_status_complete
            ExchangeOrderStatus.REFUNDED -> R.drawable.ic_status_complete
        }
    }

    private fun setStatusTitle(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.string.Swap_Details_Status_Pending
            ExchangeOrderStatus.COMPLETE -> R.string.Swap_Details_Status_Complete
            ExchangeOrderStatus.FAILED -> R.string.Swap_Details_Status_Failed
            ExchangeOrderStatus.REFUNDED -> R.string.Swap_Details_Status_Refunded
        }
    }

    private fun copyToClipboard(data: String) {
        BRClipboardManager.putClipboard(data)

        FabriikToastUtil.showInfo(
            binding.root, getString(R.string.Swap_Details_Copied)
        )
    }
}