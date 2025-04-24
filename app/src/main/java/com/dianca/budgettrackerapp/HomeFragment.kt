package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.dianca.budgettrackerapp.databinding.FragmentHomeBinding

//HomeFragment to display user welcome and navigate to different activities
class HomeFragment : Fragment() {

    //firebase authentication instance
    private lateinit var auth: FirebaseAuth
    //binding variable to handle UI elements
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //inflate the fragment's layout and initialize necessary views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //initialize firebase authentication
        auth = FirebaseAuth.getInstance()

        //get currently authenticated user
        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            //extract username from email
            val username = email.substringBefore("@")
            binding.tvWelcome.text = "Welcome, ${username.capitalize()}!"
        }

        //navigate to settings
        binding.imgProfile.setOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
        }

        //navigate to add category activity
        binding.imgCategories.setOnClickListener {
            startActivity(Intent(activity, AddCategoryActivity::class.java))
        }

        //navigate to add expense activity
        binding.imgAddExpense.setOnClickListener {
            startActivity(Intent(activity, AddExpenseActivity::class.java))
        }

        //navigate to budget goals activity
        binding.imgBudgetGoals.setOnClickListener {
            startActivity(Intent(activity, BudgetGoalsActivity::class.java))
        }

        //navigate to manage expenses activity
        binding.imgViewExpenses.setOnClickListener {
            startActivity(Intent(activity, ManageExpensesActivity::class.java))
        }

        //navigate to manage category activity
        binding.imgCategorySummary.setOnClickListener {
            startActivity(Intent(activity, ManageCategoryActivity::class.java))
        }

        //placeholder for navigating to graph activity
        binding.imgGraph.setOnClickListener {
            //startActivity(Intent(activity, GraphActivity::class.java))
            Toast.makeText(requireContext(), "Feature coming soon", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    //clean up the binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
