package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dianca.budgettrackerapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val accountBtn = binding.accountButton
        val notificationsBtn = binding.notificationsButton
        val reportBtn = binding.reportButton
        val rewardsBtn = binding.rewardsButton
        val logoutBtn = binding.logoutButton

        accountBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        notificationsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }

        reportBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ReportActivity::class.java))
        }

        rewardsBtn.setOnClickListener {
            Toast.makeText(context, "🏆 Viewing rewards...", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener {
            Toast.makeText(context, "🔒 Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }
}
