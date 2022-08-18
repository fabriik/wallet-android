package com.fabriik.trade.ui.features.assetselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.common.utils.FabriikToastUtil
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentAssetSelectionBinding
import kotlinx.coroutines.flow.collect

class AssetSelectionFragment : Fragment(),
    FabriikView<AssetSelectionContract.State, AssetSelectionContract.Effect> {

    private lateinit var binding: FragmentAssetSelectionBinding
    private val viewModel: AssetSelectionViewModel by viewModels()

    private val adapter = AssetSelectionAdapter {
        viewModel.setEvent(
            AssetSelectionContract.Event.AssetSelected(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_asset_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssetSelectionBinding.bind(view)

        with(binding) {
            toolbar.setBackButtonClickListener {
                viewModel.setEvent(AssetSelectionContract.Event.BackClicked)
            }

            etSearch.doAfterTextChanged {
                viewModel.setEvent(AssetSelectionContract.Event.SearchChanged(it?.toString()))
            }

            val layoutManager = LinearLayoutManager(context)
            rvAssets.adapter = adapter
            rvAssets.layoutManager = layoutManager
            rvAssets.setHasFixedSize(true)
            rvAssets.addItemDecoration(
                DividerItemDecoration(
                    context, layoutManager.orientation
                )
            )
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.setEvent(AssetSelectionContract.Event.BackClicked)
        }

        viewModel.setEvent(
            AssetSelectionContract.Event.LoadAssets
        )
    }

    override fun render(state: AssetSelectionContract.State) {
        adapter.submitList(state.adapterItems)
        binding.loadingIndicator.isVisible = state.initialLoadingVisible
    }

    override fun handleEffect(effect: AssetSelectionContract.Effect) {
        when (effect) {
            is AssetSelectionContract.Effect.Back -> {
                val bundle = bundleOf(
                    EXTRA_SELECTED_CURRENCY to effect.selectedCurrency
                )
                parentFragmentManager.setFragmentResult(effect.requestKey, bundle)
                findNavController().popBackStack()
            }

            is AssetSelectionContract.Effect.ShowToast ->
                FabriikToastUtil.showInfo(
                    parentView = binding.root,
                    message = effect.message
                )
        }
    }

    companion object {
        const val EXTRA_SELECTED_CURRENCY = "selected_currency_item"
    }
}