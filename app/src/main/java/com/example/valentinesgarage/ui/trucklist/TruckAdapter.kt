package com.example.valentinesgarage.ui.trucklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.valentinesgarage.data.model.Truck
import com.example.valentinesgarage.databinding.ItemTruckBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * RecyclerView adapter for displaying trucks in the list.
 */
class TruckAdapter(private val onTruckClick: (Truck) -> Unit) :
    ListAdapter<Truck, TruckAdapter.TruckViewHolder>(TruckDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruckViewHolder {
        val binding = ItemTruckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TruckViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TruckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TruckViewHolder(private val binding: ItemTruckBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(truck: Truck) {
            binding.tvPlateNumber.text = truck.plateNumber
            binding.tvKm.text = "KM: ${truck.kmOnArrival}"
            binding.tvCondition.text = truck.condition
            binding.tvCheckedInBy.text = "By: ${truck.checkedInByName}"
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                .format(Date(truck.timestamp))

            binding.root.setOnClickListener { onTruckClick(truck) }
        }
    }

    class TruckDiffCallback : DiffUtil.ItemCallback<Truck>() {
        override fun areItemsTheSame(oldItem: Truck, newItem: Truck) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Truck, newItem: Truck) = oldItem == newItem
    }
}
