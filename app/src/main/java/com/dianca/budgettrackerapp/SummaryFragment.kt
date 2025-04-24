package com.dianca.budgettrackerapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dianca.budgettrackerapp.databinding.FragmentSummaryBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SummaryFragment : Fragment() {

    private lateinit var binding: FragmentSummaryBinding
    private lateinit var viewModel: SummaryViewModel

    private var startDate: Long? = null
    private var endDate: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SummaryViewModel::class.java]

        //set up start date button
        binding.startDateBtn.setOnClickListener {
            showDatePicker { millis ->
                startDate = millis
                //show selected start date
                binding.startDateText.text = formatDate(millis)
            }
        }
        //set up end date button
        binding.endDateBtn.setOnClickListener {
            showDatePicker { millis ->
                endDate = millis
                //show selected end date
                binding.endDateText.text = formatDate(millis)
            }
        }

        //load summary button click
        binding.loadSummaryBtn.setOnClickListener {
            if (startDate != null && endDate != null) {
                lifecycleScope.launch {
                    //get summary results
                    val results = viewModel.getTotalsByCategory(startDate!!, endDate!!)
                    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
                    val summaryText = results.joinToString("\n") { "${it.first}: ${currencyFormat.format(it.second)}" }
                    //display results
                    binding.resultsText.text = summaryText
                }
            } else {
                //show error if dates are not selected
                Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    //show date picker and call onDateSelected when a date is chosen
    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            onDateSelected(calendar.timeInMillis)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    //format the selected date to "yyyy-MM-dd"
    private fun formatDate(millis: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date(millis))
    }
}
