package com.fabriik.registration.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.fabriik.registration.R
import com.fabriik.registration.databinding.ActivityRegistrationBinding
import kotlinx.parcelize.Parcelize
import java.lang.IllegalStateException

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val args = intent.extras?.getParcelable(EXTRA_ARGS) as Args?
            ?: throw IllegalStateException("Missing args argument")

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val data = when (args.flow) {
            RegistrationFlow.REGISTER -> R.id.fragmentEnterEmail to bundleOf()
            RegistrationFlow.RE_VERIFY -> R.id.fragmentVerifyEmail to bundleOf(
                EXTRA_EMAIL to args.email,
                EXTRA_FLOW to args.flow
            )
        }

        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_registration)
        graph.startDestination = data.first
        navHostFragment.navController.setGraph(graph, data.second)
    }

    override fun onBackPressed() {
        //method is disabled for this activity
    }

    companion object {
        private const val EXTRA_ARGS = "extra_args"
        private const val EXTRA_FLOW = "flow"
        private const val EXTRA_EMAIL = "email"

        const val REQUEST_CODE = 1232
        const val RESULT_VERIFIED = 1232_1

        fun getStartIntent(context: Context, args: Args) : Intent {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.putExtra(EXTRA_ARGS, args)
            return intent
        }
    }

    @Parcelize
    data class Args(
        val flow: RegistrationFlow,
        val email: String?,
    ): Parcelable
}

enum class RegistrationFlow {
    REGISTER,
    RE_VERIFY
}