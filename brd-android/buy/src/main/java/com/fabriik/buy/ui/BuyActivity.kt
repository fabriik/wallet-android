package com.fabriik.buy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.buy.R
import com.fabriik.buy.databinding.ActivityBuyBinding

class BuyActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ActivityBuyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph_buy)
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, BuyActivity::class.java)
        }
    }
}