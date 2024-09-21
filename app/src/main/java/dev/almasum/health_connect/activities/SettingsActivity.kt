package dev.almasum.health_connect.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.almasum.health_connect.databinding.ActivitySettingsBinding
import dev.almasum.health_connect.utils.AlarmHelper
import dev.almasum.health_connect.utils.Prefs

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"
        supportActionBar?.elevation = 10f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.minuteEt.setText(Prefs.interval.toString())

        binding.saveBtn.setOnClickListener {
            save()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun save(): Boolean {
        try {
            val minute = binding.minuteEt.text.toString().toLong()
            if (minute < 30) {
                Toast.makeText(this, "Please enter a number greater than 30", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            Prefs.interval = minute
            AlarmHelper.setSingleAlarm(this)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}