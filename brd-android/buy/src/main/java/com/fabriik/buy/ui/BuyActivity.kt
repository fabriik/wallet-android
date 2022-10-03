package com.fabriik.buy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.buy.R
import com.fabriik.buy.data.enums.BuyDetailsFlow
import com.fabriik.buy.databinding.ActivityBuyBinding
import com.fabriik.buy.ui.features.addcard.AddCardFlow
import kotlinx.parcelize.Parcelize

class BuyActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityBuyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val args = intent.extras?.getParcelable(EXTRA_ARGS) as StartDestinationRequest?
        checkNotNull(args) { "Missing required data" }

        navigateToScreen(
            startDestination = args.destinationId,
            bundle = args.bundle
        )
    }

    private fun navigateToScreen(startDestination: Int, bundle: Bundle) {
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_buy)
        graph.startDestination = startDestination
        navHostFragment.navController.setGraph(graph, bundle)
    }

    companion object {
        private const val EXTRA_ARGS = "extra"
        private const val EXTRA_FLOW = "flow"
        private const val EXTRA_EXCHANGE_ID = "exchangeId"

        @JvmStatic
        fun getDefaultStartIntent(context: Context) =
            getStartIntent(context, StartDestinationRequest.Default)

        @JvmStatic
        fun getStartIntentForPaymentMethod(context: Context) =
            getStartIntent(context, StartDestinationRequest.PaymentMethod)

        @JvmStatic
        fun getStartIntentForBuyDetails(context: Context, exchangeId: String) =
            getStartIntent(context, StartDestinationRequest.BuyDetails(exchangeId))

        private fun getStartIntent(context: Context, request: StartDestinationRequest): Intent {
            val intent = Intent(context, BuyActivity::class.java).apply {
                putExtra(EXTRA_ARGS, request)
            }
            return intent
        }
    }

    private sealed class StartDestinationRequest(val destinationId: Int, val bundle: Bundle) : Parcelable {

        @Parcelize
        object Default : StartDestinationRequest(
            R.id.fragmentBuyInput, bundleOf()
        )

        @Parcelize
        object PaymentMethod : StartDestinationRequest(
            R.id.fragmentPaymentMethod, bundleOf(
                EXTRA_FLOW to AddCardFlow.PROFILE
            )
        )

        @Parcelize
        class BuyDetails(val exchangeId: String) : StartDestinationRequest(
            R.id.fragmentBuyDetails, bundleOf(
                EXTRA_EXCHANGE_ID to exchangeId,
                EXTRA_FLOW to BuyDetailsFlow.TRANSACTIONS
            )
        )
    }
}