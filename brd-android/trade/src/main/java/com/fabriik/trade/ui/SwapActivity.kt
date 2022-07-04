package com.fabriik.trade.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fabriik.trade.databinding.ActivitySwapBinding

class SwapActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SwapActivity::class.java)
        }
    }
}