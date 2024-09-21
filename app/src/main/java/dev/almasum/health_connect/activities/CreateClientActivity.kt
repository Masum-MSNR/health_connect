package dev.almasum.health_connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import dev.almasum.health_connect.databinding.ActivityCreateClientBinding
import dev.almasum.health_connect.dialogs.LoadingDialog
import dev.almasum.health_connect.network.WebService
import dev.almasum.health_connect.network.pojo.ClientResponse
import dev.almasum.health_connect.network.pojo.ResponseEntity
import dev.almasum.health_connect.utils.Prefs
import retrofit2.Callback
import retrofit2.Response

class CreateClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateClientBinding
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Create Client"
        supportActionBar?.elevation = 10f

        loadingDialog = LoadingDialog()
        loadingDialog.isCancelable = false

        binding.createClientBt.setOnClickListener {
            if (validate()) {
                loadingDialog.show(supportFragmentManager, "dialog")
                val call = WebService.getClient().createClient(
                    binding.firstNameEt.text.toString(),
                    binding.lastNameEt.text.toString(),
                    binding.phoneEt.text.toString(),
                    binding.emailEt.text.toString(),
                    "Android",
                    1
                )
                call.enqueue(object : Callback<ResponseEntity> {
                    override fun onResponse(
                        call: retrofit2.Call<ResponseEntity>,
                        response: Response<ResponseEntity>
                    ) {
                        loadingDialog.dismiss()
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                Prefs.firstName = binding.firstNameEt.text.toString()
                                Prefs.lastName = binding.lastNameEt.text.toString()
                                Prefs.phone = binding.phoneEt.text.toString()
                                Prefs.email = binding.emailEt.text.toString()
                                val jsonString = Gson().toJson(response.body()!!.responseObjects[0])
                                val clientResponse: ClientResponse =
                                    Gson().fromJson(jsonString, ClientResponse::class.java)
                                Prefs.clientId = clientResponse.clientId.toString()


                                Toast.makeText(
                                    this@CreateClientActivity,
                                    "Client created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(
                                    this@CreateClientActivity,
                                    HealthDataActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@CreateClientActivity,
                                    "Something went wrong! Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CreateClientActivity,
                                "Something went wrong! Please try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<ResponseEntity>, t: Throwable) {
                        println(t.message)
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@CreateClientActivity,
                            "Something went wrong! Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }
        }

    }

    private fun validate(): Boolean {
        if (binding.firstNameEt.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show()
            binding.firstNameEt.requestFocus()
            return false
        } else if (binding.lastNameEt.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show()
            binding.lastNameEt.requestFocus()
            return false
        } else if (binding.phoneEt.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show()
            binding.phoneEt.requestFocus()
            return false
        } else if (binding.emailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
            binding.emailEt.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString())
                .matches()
        ) {
            Toast.makeText(this, "Enter Valid Email", Toast.LENGTH_SHORT).show()
            binding.emailEt.requestFocus()
            return false
        }
        return true
    }
}