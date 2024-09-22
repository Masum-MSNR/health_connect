package dev.almasum.health_connect.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.almasum.health_connect.R
import dev.almasum.health_connect.databinding.ActivityHealthDataBinding
import dev.almasum.health_connect.dialogs.AlarmPermissionDialog
import dev.almasum.health_connect.utils.AlarmHelper
import dev.almasum.health_connect.utils.DataUploader
import dev.almasum.health_connect.utils.Prefs
import dev.almasum.health_connect.viewModels.HealthDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HealthDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHealthDataBinding
    private lateinit var viewModel: HealthDataViewModel
    private lateinit var alarmPermissionDialog: AlarmPermissionDialog

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHealthDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HealthDataViewModel::class.java]
        viewModel.initHealthConnectManager(this)

        binding.bottomNav.background = null
        binding.bottomNav.menu.getItem(2).isEnabled = false
        supportActionBar?.title = "Health Data"
        supportActionBar?.elevation = 10f

        binding.welcomeText.text = "Hi, ${Prefs.firstName}"

        alarmPermissionDialog =
            AlarmPermissionDialog("Schedule alarm permission is required to update data to server periodically.") {
                val intentX = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intentX.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intentX)
                alarmPermissionDialog.dismiss()
            }
        alarmPermissionDialog.isCancelable = false


        CoroutineScope(Dispatchers.IO).launch {
            viewModel.readSteps()
            viewModel.readHeartRate()
            viewModel.readRespiratoryRate()
            viewModel.readBloodPressure()
            viewModel.readOxygenLevel()
            viewModel.readBodyTemperature()
            DataUploader.uploadSteps(this@HealthDataActivity) {
            }
            DataUploader.uploadOxygen(this@HealthDataActivity) {
            }
        }

        binding.fab.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.readSteps()
                viewModel.readHeartRate()
                viewModel.readRespiratoryRate()
                viewModel.readBloodPressure()
                viewModel.readOxygenLevel()
                viewModel.readBodyTemperature()
                DataUploader.uploadSteps(this@HealthDataActivity) {
                }
                DataUploader.uploadOxygen(this@HealthDataActivity) {
                }
            }
        }

        viewModel.steps.observe(this) {
            var steps = 0

            if (it != null && it != "--") {
                steps = it.toInt()
            }
            var left = 0
            if (steps <= 10000) {
                left = 10000 - steps
            }
            binding.stepsOutOfTotal.text = "$steps of 10000 steps"
            binding.stepsLeft.text = "$left left"
            var parcent = 0
            if (steps != 0) {
                parcent = steps * 100 / 10000
            }
            binding.stepsProgress.progress = parcent
            binding.stepsPercent.text = "$parcent%"
        }

        viewModel.heartRate.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.heartRate.text = "--"
                return@observe
            }
            binding.heartRate.text = "${it} bpm"
        }

        viewModel.respiratoryRate.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.respiratoryRate.text = "--"
                return@observe
            }
            binding.respiratoryRate.text = "${it} bpm"
        }

        viewModel.systolicBp.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.systolic.text = "--"
                return@observe
            }
            binding.systolic.text = "${it} mmHg"
        }

        viewModel.diastolicBp.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.diastolic.text = "--"
                return@observe
            }
            binding.diastolic.text = "${it} mmHg"
        }

        viewModel.oxygenLevel.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.oxygenLevel.text = "--"
                return@observe
            }
            binding.oxygenLevel.text = "${it}%"
        }

        viewModel.bodyTemp.observe(this) {
            if (it == null || it == "--" || it == "0.0") {
                binding.bodyTemp.text = "--"
                return@observe
            }
            binding.bodyTemp.text = "${it}°C"
        }

        val dateFormatter = SimpleDateFormat("MMM, dd yyyy", Locale.getDefault())
        val date = dateFormatter.format(java.util.Date())
        binding.todayDate.text = date
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                alarmPermissionDialog.show(supportFragmentManager, "alarmPermissionDialog")
            } else {
                AlarmHelper.setSingleAlarm(this)
            }
        } else {
            AlarmHelper.setSingleAlarm(this)
        }
    }
}