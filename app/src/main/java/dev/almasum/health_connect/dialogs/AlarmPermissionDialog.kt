package dev.almasum.health_connect.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import dev.almasum.health_connect.R
import dev.almasum.health_connect.databinding.DialogConfirmationBinding

class AlarmPermissionDialog(val text: String, val onComplete: () -> Unit) :
    AppCompatDialogFragment() {
    private lateinit var binding: DialogConfirmationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.Dialog)
        binding = DialogConfirmationBinding.inflate(getLayoutInflater())
        binding.tvTitle.text = text
        binding.tbtnNo.setOnClickListener { dismiss() }

        binding.tbtnYes.setOnClickListener {
            onComplete.invoke()
        }
        builder.setView(binding.getRoot())
        return builder.create()
    }
}


