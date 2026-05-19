package com.example.valentinesgarage.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.valentinesgarage.databinding.ItemReportBinding

/**
 * Data class representing a single report entry.
 */
data class ReportItem(
    val id: String = "",
    val plateNumber: String = "",
    val employeeName: String = "",
    val taskDescription: String = "",
    val notes: String = "",
    val isDone: Boolean = false,
    val timestamp: Long = 0L
)

/**
 * Adapter for displaying report items.
 */
class ReportAdapter : ListAdapter<ReportItem, ReportAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReportItem) {
            binding.tvPlate.text = "Truck: ${item.plateNumber}"
            binding.tvEmployee.text = "Employee: ${item.employeeName}"
            binding.tvTask.text = "Task: ${item.taskDescription}"
            binding.tvNotes.text = if (item.notes.isNotEmpty()) "Notes: ${item.notes}" else "No notes"
            binding.tvStatus.text = if (item.isDone) "✓ Completed" else "✗ Pending"
            binding.tvStatus.setTextColor(
                if (item.isDone)
                    binding.root.context.getColor(android.R.color.holo_green_dark)
                else
                    binding.root.context.getColor(android.R.color.holo_red_dark)
            )
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<ReportItem>() {
        override fun areItemsTheSame(oldItem: ReportItem, newItem: ReportItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReportItem, newItem: ReportItem) = oldItem == newItem
    }
}
