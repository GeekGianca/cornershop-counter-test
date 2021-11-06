package com.cornershop.counterstest.presentation.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cornershop.counterstest.core.SharedPreferenceManager
import com.cornershop.counterstest.databinding.ActivityWelcomeBinding
import com.cornershop.counterstest.presentation.main.MainActivity

class WelcomeActivity : AppCompatActivity() {
    private var _binding: ActivityWelcomeBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (SharedPreferenceManager.startState(this)) {
            initMainView()
        }
        binding.contentWelcome.buttonStart.setOnClickListener {
            SharedPreferenceManager.startState(this@WelcomeActivity, true)
            initMainView()
        }
    }

    private fun initMainView() {
        startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        finish()
    }
}
