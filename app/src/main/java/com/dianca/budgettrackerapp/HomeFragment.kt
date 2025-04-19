package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.dianca.budgettrackerapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            val username = email.substringBefore("@")
            binding.tvWelcome.text = "Welcome, ${username.capitalize()}!"
        }

        binding.imgProfile.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }

        binding.imgCategories.setOnClickListener {
            // startActivity(Intent(activity, CategoriesActivity::class.java))
        }

        binding.imgAddExpense.setOnClickListener {
            startActivity(Intent(activity, AddExpenseActivity::class.java))
        }

        binding.imgBudgetGoals.setOnClickListener {
            startActivity(Intent(activity, BudgetGoalsActivity::class.java))
        }

        binding.imgViewExpenses.setOnClickListener {
             startActivity(Intent(activity, ViewExpensesActivity::class.java))
        }

        binding.imgCategorySummary.setOnClickListener {
            // startActivity(Intent(activity, CategorySummaryActivity::class.java))
        }

        binding.imgGraph.setOnClickListener {
            // startActivity(Intent(activity, GraphActivity::class.java))
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
