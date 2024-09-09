package dev.almasum.health_connect.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.almasum.health_connect.databinding.ActivityHealthDataBinding

class HealthDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHealthDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Health Data"
    }
}