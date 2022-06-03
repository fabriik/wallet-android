package com.fabriik.kyc.ui.features.countryselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabriik.common.ui.base.FabriikView
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.FragmentCountrySelectionBinding
import kotlinx.coroutines.flow.collect

class CountrySelectionFragment : Fragment(),
    FabriikView<CountrySelectionContract.State, CountrySelectionContract.Effect> {

    private lateinit var binding: FragmentCountrySelectionBinding
    private val viewModel: CountrySelectionViewModel by viewModels()

    private val adapter = CountrySelectionAdapter {
        viewModel.setEvent(
            CountrySelectionContract.Event.CountrySelected(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_country_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCountrySelectionBinding.bind(view)

        with(binding) {
            btnBack.setOnClickListener {
                viewModel.setEvent(CountrySelectionContract.Event.BackClicked)
            }

            btnDismiss.setOnClickListener {
                viewModel.setEvent(CountrySelectionContract.Event.DismissClicked)
            }

            etSearch.doAfterTextChanged {
                viewModel.setEvent(CountrySelectionContract.Event.SearchChanged(it?.toString()))
            }

            val layoutManager = LinearLayoutManager(context)
            rvCountries.adapter = adapter
            rvCountries.layoutManager = layoutManager
            rvCountries.setHasFixedSize(true)
            rvCountries.addItemDecoration(
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

        viewModel.setEvent(
            CountrySelectionContract.Event.LoadCountries
        )
    }

    override fun render(state: CountrySelectionContract.State) {
        adapter.submitList(state.adapterItems)
    }

    override fun handleEffect(effect: CountrySelectionContract.Effect) {
        when (effect) {
            is CountrySelectionContract.Effect.Dismiss ->
                requireActivity().finish()
            is CountrySelectionContract.Effect.Back -> {
                val bundle = bundleOf(
                    EXTRA_SELECTED_COUNTRY to effect.selectedCountry
                )
                parentFragmentManager.setFragmentResult(effect.requestKey, bundle)
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        const val EXTRA_SELECTED_COUNTRY = "selected_country_item"
    }
}