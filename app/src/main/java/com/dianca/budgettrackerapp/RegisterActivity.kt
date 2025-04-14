package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.dianca.budgettrackerapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //using firebase for authorization
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            //allow user to enter input into fields
            val username = binding.registerUsernameEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString().trim()
            val confirmPassword = binding.ConfirmRegisterPasswordEditText.text.toString().trim()

            //handling -> if empty prompt to enter input
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                //confirm password message pop up if it does not match password
                Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show()
            } else if (!isPasswordValid(password)) {
                Toast.makeText(
                    this,
                    "Password does not meet the required criteria", Toast.LENGTH_LONG).show()
            } else {
                //all details entered and valid -> register user
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this,
                                "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        //link to login screen
        binding.goToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    //method -> to validate password
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$"
        return password.matches(Regex(passwordPattern))
    }
}