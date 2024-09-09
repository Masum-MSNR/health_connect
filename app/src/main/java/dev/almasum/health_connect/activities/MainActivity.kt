package dev.almasum.health_connect.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.almasum.health_connect.R
import dev.almasum.health_connect.data.MIN_SUPPORTED_SDK
import dev.almasum.health_connect.databinding.ActivityMainBinding
import dev.almasum.health_connect.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var healthConnectAvailabilityObserver: Observer<Int>
    private lateinit var viewModel: MainViewModel
    private val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()
    private val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(viewModel.permissions)) {
                // Permissions successfully granted
            } else {
                // Lack of required permissions
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.initHealthConnectManager(this)

        supportActionBar?.title = "Health Connect"

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
                    binding.grantPermissions.visibility = View.GONE
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
                    binding.grantPermissions.visibility = View.GONE
                    binding.secondaryText.setOnClickListener {
                        onLearnMoreClick()
                    }
                }
            }
        }

        viewModel.availability.observe(this, healthConnectAvailabilityObserver)
        viewModel.permissionGranted.observe(this) {
            if (it) {
                binding.grantPermissions.visibility = View.GONE
                binding.healthData.visibility = View.VISIBLE
                binding.healthData.setOnClickListener {
                    val intent = Intent(this, HealthDataActivity::class.java)
                    startActivity(intent)
                }
            } else {
                binding.healthData.visibility = View.GONE
                binding.grantPermissions.visibility = View.VISIBLE
                binding.grantPermissions.setOnClickListener {
                    onGrantPermissionsClick()
                }
            }
        }
    }

    private fun onGrantPermissionsClick() {
        requestPermissions.launch(viewModel.permissions)
        Log.v("MainActivity", "Requesting permissions")
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

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.checkPermission()
        }
        viewModel.checkAvailability(this)
    }
}