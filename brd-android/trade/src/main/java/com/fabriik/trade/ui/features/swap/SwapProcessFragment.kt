package com.fabriik.trade.ui.features.swap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentSwapProcessBinding

class SwapProcessFragment : Fragment() {

    private lateinit var binding: FragmentSwapProcessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swap_process, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSwapProcessBinding.bind(view)
        val args = SwapProcessFragmentArgs.fromBundle(requireArguments())
        with(binding) {
            tvProcessingTitle.text = getString(R.string.Swap_Process_Header, args.coinFrom, args.coinTo)
            tvProcessingBody.text = getString(R.string.Swap_Process_Body, args.coinTo)

            btnHome.setOnClickListener {
                requireActivity().finish()
            }
            btnDetails.setOnClickListener {
                findNavController().navigate(SwapProcessFragmentDirections.actionFragmentSwapProcessingToFragmentSwapDetails())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            //User shouldn't be allowed to go back
        }
    }
}