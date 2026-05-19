package com.example.valentinesgarage.ui.reports

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.valentinesgarage.databinding.ActivityReportsBinding
import com.example.valentinesgarage.viewmodel.ReportsViewModel

/**
 * Reports screen for Valentine (admin) to view what each employee did per truck.
 */
class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private lateinit var viewModel: ReportsViewModel
    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Reports"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[ReportsViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        viewModel.loadReports()
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter()
        binding.rvReports.layoutManager = LinearLayoutManager(this)
        binding.rvReports.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.reports.observe(this) { reports ->
            adapter.submitList(reports)
            binding.tvEmpty.visibility = if (reports.isEmpty()) View.VISIBLE else View.GONE
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
