package com.valentinesgarage.ui.checkin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.valentinesgarage.R
import com.valentinesgarage.data.model.ConditionRating
import com.valentinesgarage.databinding.FragmentCheckInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckInFragment : Fragment() {

    private var _binding: FragmentCheckInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CheckInViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate condition spinner
        val conditions = ConditionRating.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, conditions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCondition.adapter = adapter

        binding.btnSubmitCheckIn.setOnClickListener {
            val selectedCondition = ConditionRating.values()[binding.spinnerCondition.selectedItemPosition]
            viewModel.checkInTruck(
                registration = binding.etRegistration.text.toString(),
                make = binding.etMake.text.toString(),
                model = binding.etModel.text.toString(),
                kilometers = binding.etKilometers.text.toString(),
                condition = selectedCondition,
                conditionNotes = binding.etConditionNotes.text.toString()
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is CheckInViewModel.CheckInState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                    }
                    is CheckInViewModel.CheckInState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnSubmitCheckIn.isEnabled = false
                        binding.tvError.visibility = View.GONE
                    }
                    is CheckInViewModel.CheckInState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        // Navigate to job detail after successful check-in
                        val bundle = Bundle().apply {
                            putString("jobId", state.jobId)
                            putString("truckId", state.truckId)
                        }
                        findNavController().navigate(R.id.action_checkIn_to_jobDetail, bundle)
                    }
                    is CheckInViewModel.CheckInState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmitCheckIn.isEnabled = true
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = state.message
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
