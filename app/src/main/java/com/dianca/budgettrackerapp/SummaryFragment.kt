package com.dianca.budgettrackerapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.databinding.FragmentSummaryBinding
import com.dianca.budgettrackerapp.viewmodel.SummaryViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    //viewModel instance scoped to this fragment
    private val viewModel: SummaryViewModel by viewModels()
    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    //called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePickers()

        //load summary when button is clicked
        binding.loadSummaryBtn.setOnClickListener {
            if (startDate != null && endDate != null) {
                loadSummary()
            } else {
                //show error if dates are not selected
                Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //set up date pickers for start and end dates
    private fun setupDatePickers() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        //set up start date button
        binding.startDateBtn.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, y, m, d ->
                startDate = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0) }
                binding.startDateBtn.text = sdf.format(startDate!!.time)
            }, year, month, day).show()
        }

        //set up end date button
        binding.endDateBtn.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, y, m, d ->
                endDate = Calendar.getInstance().apply { set(y, m, d, 23, 59, 59) }
                binding.endDateBtn.text = sdf.format(endDate!!.time)
            }, year, month, day).show()
        }
    }

    //load and display summary based on selected date range
    private fun loadSummary() {
        val start = startDate ?: return
        val end = endDate ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                //get summary results from ViewModel
                val results = viewModel.getTotalsByCategory(start, end)
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

                val summaryText = results.joinToString("\n") {
                    "${it.first}: ${currencyFormat.format(it.second)}"
                }

                //display results
                binding.resultsText.text = summaryText
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load summary", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //clear binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
