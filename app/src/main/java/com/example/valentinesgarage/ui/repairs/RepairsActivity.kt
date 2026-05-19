package com.example.valentinesgarage.ui.repairs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valentinesgarage.databinding.ActivityRepairsBinding
import com.example.valentinesgarage.viewmodel.RepairsViewModel

/**
 * Screen for mechanics to collaboratively tick off and write notes on repair tasks.
 */
class RepairsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepairsBinding
    private lateinit var viewModel: RepairsViewModel
    private lateinit var adapter: RepairTaskAdapter
    private var truckId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        truckId = intent.getStringExtra("truckId") ?: ""
        val plateNumber = intent.getStringExtra("plateNumber") ?: ""

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Repairs - $plateNumber"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[RepairsViewModel::class.java]

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        viewModel.loadTasks(truckId)
    }

    private fun setupRecyclerView() {
        adapter = RepairTaskAdapter { task ->
            viewModel.updateTask(task)
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnAddTask.setOnClickListener {
            val description = binding.etNewTask.text.toString().trim()
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a task description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addTask(truckId, description)
            binding.etNewTask.setText("")
        }
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            adapter.submitList(tasks)
            binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
