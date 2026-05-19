package com.example.valentinesgarage.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.valentinesgarage.data.model.RepairTask
import com.example.valentinesgarage.databinding.ItemRepairTaskBinding

/**
 * Adapter for displaying repair tasks for a truck.
 */
class RepairTaskAdapter(private val onTaskUpdate: (RepairTask) -> Unit) :
    ListAdapter<RepairTask, RepairTaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemRepairTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemRepairTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: RepairTask) {
            binding.tvDescription.text = task.description
            binding.cbDone.isChecked = task.isDone
            binding.etNotes.setText(task.notes)

            if (task.isDone) {
                binding.tvDoneBy.text = "Done by: ${task.doneByName}"
                binding.tvDoneBy.visibility = android.view.View.VISIBLE
            } else {
                binding.tvDoneBy.visibility = android.view.View.GONE
            }

            binding.btnSave.setOnClickListener {
                val updatedTask = task.copy(
                    isDone = binding.cbDone.isChecked,
                    notes = binding.etNotes.text.toString().trim()
                )
                onTaskUpdate(updatedTask)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<RepairTask>() {
        override fun areItemsTheSame(oldItem: RepairTask, newItem: RepairTask) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RepairTask, newItem: RepairTask) = oldItem == newItem
    }
}
