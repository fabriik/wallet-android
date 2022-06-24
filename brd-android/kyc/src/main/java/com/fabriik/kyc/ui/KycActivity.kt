package com.fabriik.kyc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.kyc.R
import com.fabriik.kyc.databinding.ActivityKycBinding

class KycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController.setGraph(R.navigation.nav_graph_kyc, intent.extras)
    }

    companion object {
        const val REQUEST_CODE = 1231
        const val RESULT_DATA_UPDATED = 1231_1

        fun getStartIntent(context: Context): Intent {
            return Intent(context, KycActivity::class.java)
        }
    }
}