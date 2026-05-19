package com.example.valentinesgarage.ui.checkin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.valentinesgarage.databinding.ActivityCheckInBinding
import com.example.valentinesgarage.viewmodel.CheckInViewModel

/**
 * Screen for checking in a truck at the garage.
 * Captures plate number, km driven, and vehicle condition.
 */
class CheckInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckInBinding
    private lateinit var viewModel: CheckInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Check In Truck"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[CheckInViewModel::class.java]

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnCheckIn.setOnClickListener {
            val plate = binding.etPlateNumber.text.toString().trim()
            val km = binding.etKm.text.toString().trim()
            val condition = binding.etCondition.text.toString().trim()

            if (plate.isEmpty() || km.isEmpty() || condition.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.checkInTruck(plate, km.toInt(), condition)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnCheckIn.isEnabled = !isLoading
        }

        viewModel.checkInSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Truck checked in successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
