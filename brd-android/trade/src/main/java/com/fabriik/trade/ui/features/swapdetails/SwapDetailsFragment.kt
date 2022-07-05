package com.fabriik.trade.ui.features.swapdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapDetailsBinding

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

    override fun render(state: SwapDetailsContract.State) {
        TODO("Not yet implemented")
    }

    override fun handleEffect(effect: SwapDetailsContract.Effect) {
        TODO("Not yet implemented")
    }
}