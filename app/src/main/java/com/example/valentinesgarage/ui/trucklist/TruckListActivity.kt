package com.example.valentinesgarage.ui.trucklist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valentinesgarage.R
import com.example.valentinesgarage.data.model.Truck
import com.example.valentinesgarage.databinding.ActivityTruckListBinding
import com.example.valentinesgarage.ui.checkin.CheckInActivity
import com.example.valentinesgarage.ui.login.LoginActivity
import com.example.valentinesgarage.ui.repairs.RepairsActivity
import com.example.valentinesgarage.ui.reports.ReportsActivity
import com.example.valentinesgarage.viewmodel.TruckListViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Main screen showing all checked-in trucks.
 */
class TruckListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTruckListBinding
    private lateinit var viewModel: TruckListViewModel
    private lateinit var adapter: TruckAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTruckListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Valentine's Garage"

        viewModel = ViewModelProvider(this)[TruckListViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        viewModel.loadTrucks()
    }

    private fun setupRecyclerView() {
        adapter = TruckAdapter { truck ->
            // Navigate to repairs screen when truck is clicked
            val intent = Intent(this, RepairsActivity::class.java)
            intent.putExtra("truckId", truck.id)
            intent.putExtra("plateNumber", truck.plateNumber)
            startActivity(intent)
        }
        binding.rvTrucks.layoutManager = LinearLayoutManager(this)
        binding.rvTrucks.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabCheckIn.setOnClickListener {
            startActivity(Intent(this, CheckInActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.trucks.observe(this) { trucks ->
            adapter.submitList(trucks)
            binding.tvEmpty.visibility = if (trucks.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTrucks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
