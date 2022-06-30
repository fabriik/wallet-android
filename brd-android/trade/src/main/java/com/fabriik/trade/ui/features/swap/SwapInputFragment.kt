package com.fabriik.trade.ui.features.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapInputBinding
import com.fabriik.trade.ui.customview.SwapCardView
import kotlinx.coroutines.flow.collect

class SwapInputFragment : Fragment(),
    FabriikView<SwapInputContract.State, SwapInputContract.Effect> {

    private lateinit var binding: FragmentSwapInputBinding
    private val viewModel: SwapInputViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swap_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapInputBinding.bind(view)

        with(binding) {
            toolbar.setDismissButtonClickListener {
                viewModel.setEvent(SwapInputContract.Event.DismissClicked)
            }

            //todo: for test only
            tvTimer.text = "00:15s"
            tvRateValue.text = "1 BSV = 0.0000333 BTC"
            cvSwap.setFiatCurrency("USD")
            cvSwap.setOriginCurrency("BSV")
            cvSwap.setDestinationCurrency("BTC")

            cvSwap.setCallback(object: SwapCardView.Callback{
                override fun onBuyingCurrencySelectorClicked() {
                    viewModel.setEvent(SwapInputContract.Event.OriginCurrencyClicked)
                }

                override fun onSellingCurrencySelectorClicked() {
                    viewModel.setEvent(SwapInputContract.Event.DestinationCurrencyClicked)
                }
            })
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

    override fun render(state: SwapInputContract.State) {
    }

    override fun handleEffect(effect: SwapInputContract.Effect) {
        when (effect) {
            SwapInputContract.Effect.Dismiss ->
                requireActivity().finish()

            SwapInputContract.Effect.OriginSelection ->
                Toast.makeText(context, "Origin currency selected", Toast.LENGTH_LONG).show()

            SwapInputContract.Effect.DestinationSelection ->
                Toast.makeText(context, "Destination currency selected", Toast.LENGTH_LONG).show()
        }
    }
}