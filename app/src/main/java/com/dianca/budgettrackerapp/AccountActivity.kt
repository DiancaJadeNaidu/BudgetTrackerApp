package com.dianca.budgettrackerapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityAccountBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        //setting the welcome message and username as well as email display
        user?.let {
            val email = it.email ?: "Unknown"
            //the username is basically the email but before the '@'
            binding.welcomeText.text = "Welcome, ${email.substringBefore("@")}"
            binding.emailDisplay.text = "Email: $email"
        } ?: run {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        //save new password with re-authentication
        binding.btnSavePassword.setOnClickListener {
            val currentPassword = binding.currentPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()

            if (currentPassword.isNotEmpty() && newPassword.length >= 6) {
                val email = user?.email
                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)

                    user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            user?.updatePassword(newPassword)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Current password incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Enter valid credentials (new password ≥ 6 chars)", Toast.LENGTH_SHORT).show()
            }
        }

        // delete account confirmation
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    user?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, RegisterActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Account deletion failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        //change profile picture (mocked)
        binding.btnChangeProfilePicture.setOnClickListener {
            Toast.makeText(this, "Feature coming soon: Choose your own profile image", Toast.LENGTH_SHORT).show()
        }
    }
}
