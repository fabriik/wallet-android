package com.fabriik.swap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fabriik.swap.R
import com.fabriik.swap.databinding.FragmentSelectAmountBinding
import java.math.BigDecimal

class SelectAmountFragment : Fragment() {

    private lateinit var binding: FragmentSelectAmountBinding
    private lateinit var viewModel: SwapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_select_amount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectAmountBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())
            .get(SwapViewModel::class.java)

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(
                R.id.action_swap_preview
            )
        }

        binding.etPayWith.doAfterTextChanged {
            viewModel.onAmountChanged(
                BigDecimal.TEN
            )
        }
    }
}