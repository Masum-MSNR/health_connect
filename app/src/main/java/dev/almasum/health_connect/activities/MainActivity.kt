package dev.almasum.health_connect.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dev.almasum.health_connect.R
import dev.almasum.health_connect.databinding.ActivityMainBinding
import dev.almasum.health_connect.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var healthConnectAvailabilityObserver: Observer<Int>
    private lateinit var viewModel: MainViewModel
    private val requestPermissionActivityContract =
        PermissionController.createRequestPermissionResultContract()
    private val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { granted ->
            if (granted.containsAll(viewModel.permissions)) {
                Toast.makeText(this, "All Permission Granted", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        supportActionBar?.hide()

        healthConnectAvailabilityObserver = Observer {
            when (it) {
                SDK_AVAILABLE -> {
                    if (!viewModel.healthConnectInitiated) {
                        viewModel.initHealthConnectManager(this)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.checkPermission()
                        }
                    }
                    binding.primaryText.text = getString(R.string.installed_welcome_message)
                    binding.secondaryText.text = ""
                    binding.secondaryText.visibility = View.GONE
                }

                SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                    if (viewModel.healthConnectInitiated) {
                        viewModel.cleanHealthConnectManager()
                    }
                    binding.primaryText.text = getString(R.string.not_installed_description)
                    binding.secondaryText.text = getString(R.string.not_installed_link_text)
                    binding.secondaryText.visibility = View.VISIBLE
                    binding.grantPermissions.visibility = View.GONE
                    binding.healthData.visibility = View.GONE
                    binding.secondaryText.setOnClickListener {
                        onInstallClick()
                    }
                }

                else -> {
                    binding.primaryText.text =
                        getString(R.string.not_supported_description).replace(
                            "_SDK_",
                            Build.VERSION_CODES.O_MR1.toString()
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
                    val intent = Intent(this, CreateClientActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
            if (viewModel.healthConnectInitiated) {
                try {
                    viewModel.checkPermission()
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
        viewModel.checkAvailability(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 99)
            }
        }
    }
}