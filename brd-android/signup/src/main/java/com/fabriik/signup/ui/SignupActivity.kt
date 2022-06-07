package com.fabriik.signup.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.signup.R
import com.fabriik.signup.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var navHostFragment: NavHostFragment

    private val topLevelDestinations = arrayOf(
        R.id.fragmentLogIn,
        R.id.fragmentKycCompleted,
        R.id.fragmentResetCompleted
    )

    private val navigationListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        val isTopLevelDestination = topLevelDestinations.contains(destination.id)
        binding.btnBack.isVisible = !isTopLevelDestination
        binding.btnDismiss.isVisible = isTopLevelDestination

        binding.appBar.setExpanded(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener {
                findNavController(R.id.nav_host_fragment).popBackStack()
            }
            btnDismiss.setOnClickListener { finish() }
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onResume() {
        super.onResume()
        navHostFragment.navController.addOnDestinationChangedListener(navigationListener)
    }

    override fun onPause() {
        navHostFragment.navController.removeOnDestinationChangedListener(navigationListener)
        super.onPause()
    }

    fun showLoading(show: Boolean) {
        binding.loadingView.isVisible = show
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }
}