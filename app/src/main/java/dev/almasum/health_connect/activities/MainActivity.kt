package dev.almasum.health_connect.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
import androidx.lifecycle.Observer
import dev.almasum.health_connect.R
import dev.almasum.health_connect.data.HealthConnectManager
import dev.almasum.health_connect.data.MIN_SUPPORTED_SDK
import dev.almasum.health_connect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var healthConnectManager: HealthConnectManager

    private lateinit var healthConnectAvailabilityObserver: Observer<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Health Connect"
        healthConnectManager = HealthConnectManager(this)

        healthConnectAvailabilityObserver = Observer {
            when (it) {
                SDK_AVAILABLE -> {
                    binding.primaryText.text = getString(R.string.installed_welcome_message)
                    binding.secondaryText.text = ""
                    binding.secondaryText.visibility = View.GONE
                }

                SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                    binding.primaryText.text = getString(R.string.not_installed_description)
                    binding.secondaryText.text = getString(R.string.not_installed_link_text)
                    binding.secondaryText.visibility = View.VISIBLE
                    binding.secondaryText.setOnClickListener {
                        onInstallClick()
                    }
                }

                else -> {
                    binding.primaryText.text =
                        getString(R.string.not_supported_description).replace(
                            "_SDK_",
                            MIN_SUPPORTED_SDK.toString()
                        )
                    binding.secondaryText.text = getString(R.string.not_supported_link_text)
                    binding.secondaryText.visibility = View.VISIBLE
                    binding.secondaryText.setOnClickListener {
                        onLearnMoreClick()
                    }
                }
            }
        }

        healthConnectManager.availability.observe(this, healthConnectAvailabilityObserver)


    }

    private fun onLearnMoreClick() {
        val url = getString(R.string.not_supported_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)

    }


    private fun onInstallClick() {
        val url = Uri.parse(getString(R.string.market_url))
            .buildUpon()
            .appendQueryParameter("id", getString(R.string.health_connect_package))
            .appendQueryParameter("url", getString(R.string.onboarding_url))
            .build()

        startActivity(
            Intent(Intent.ACTION_VIEW, url)
        )
    }
}