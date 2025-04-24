package com.dianca.budgettrackerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dianca.budgettrackerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //inflate the layout using view binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //using firebase for authentication
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        //display user email and greeting if user is logged in
        user?.let {
            val email = it.email ?: "Unknown"
            binding.welcomeText.text = "Welcome, ${email.substringBefore("@")}"
            binding.emailDisplay.text = "Email: $email"
        } ?: run {
            //if no user found, show a toast and close the activity
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        //handle password update functionality
        binding.btnSavePassword.setOnClickListener {
            val currentPassword = binding.currentPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            if (currentPassword.isNotEmpty() && newPassword.length >= 6) {
                val email = user?.email
                if (email != null) {
                    //authenticate user again before updating password
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            //update password if re-authentication is successful
                            user?.updatePassword(newPassword)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            //message pop-up if current password is incorrect
                            Toast.makeText(requireContext(), "Current password incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Enter valid credentials (new password ≥ 6 chars)", Toast.LENGTH_SHORT).show()
            }
        }

        //handle account deletion confirmation dialog and logic
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    user?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), RegisterActivity::class.java))
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Account deletion failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        //change profile picture (mocked)
        //not yet implemented because of unknown reasons
        binding.btnChangeProfilePicture.setOnClickListener {
            Toast.makeText(requireContext(), "Feature coming soon: Choose your own profile image", Toast.LENGTH_SHORT).show()
        }
    }
}
