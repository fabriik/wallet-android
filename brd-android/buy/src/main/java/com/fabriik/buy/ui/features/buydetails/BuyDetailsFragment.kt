package com.fabriik.buy.ui.features.buydetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.breadwallet.breadbox.formatCryptoForUi
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

    private fun showLoadingState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = true
        }
    }

    private fun showErrorState() {
        with(binding) {
            content.isVisible = false
            initialLoadingIndicator.isVisible = false
        }
    }

    private fun showLoadedState(state: BuyDetailsContract.State.Loaded) {
        val data = state.data

        with(binding) {
            // status item
            icStatus.setImageResource(setStatusIcon(data.status))
            tvStatus.text = getString(setStatusTitle(data.status))

            // crypto amount item
            ivCrypto.postLoadIcon(data.destination.currency)
            tvCryptoAmount.text = data.destination.currencyAmount.formatCryptoForUi(
                data.destination.currency, 8
            )

            // purchase details items
            tvRateValue.text = "rate"
            tvPurchasedAmount.text = "amount"
            tvCreditFeeAmount.text = "card fee"
            tvNetworkFeeAmount.text = "network fee"
            tvTotalAmount.text = "total"

            // Fabriik transaction ID item
            tvOrderId.text = data.orderId

            // {cryptoCurrency} Transaction ID item
            tvCryptoTransactionIdTitle.text = getString(
                R.string.Buy_Details_CryptoTransactionId, data.destination.currency.uppercase(Locale.getDefault())
            )

            if (data.destination.transactionId.isNullOrEmpty()) {
                tvCryptoTransactionId.text = getString(R.string.Buy_Details_Status_Pending)
                tvCryptoTransactionId.setCompoundDrawablesRelative(null, null, null, null)
                tvCryptoTransactionId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_text_02))
            } else {
                tvCryptoTransactionId.text = data.destination.transactionId
                tvCryptoTransactionId.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_link_02))
            }

            // timestamp item
            val date = Date(data.timestamp)
            tvTimestamp.text = dateFormatter.format(date)

            // others
            content.isVisible = true
            initialLoadingIndicator.isVisible = false
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

    private fun setStatusIcon(status: ExchangeOrderStatus): Int {
        return when (status) {
            ExchangeOrderStatus.PENDING -> R.drawable.ic_status_pending
            ExchangeOrderStatus.FAILED -> R.drawable.ic_status_failed
            ExchangeOrderStatus.COMPLETE -> R.drawable.ic_status_complete
            ExchangeOrderStatus.REFUNDED -> R.drawable.ic_status_complete
        }
    }

    private fun copyToClipboard(data: String) {
        BRClipboardManager.putClipboard(data)

        FabriikToastUtil.showInfo(
            binding.root, getString(R.string.Swap_Details_Copied)
        )
    }
}