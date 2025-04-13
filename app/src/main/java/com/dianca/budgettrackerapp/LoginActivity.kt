package com.dianca.budgettrackerapp
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.dianca.budgettrackerapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //using firebase for authorization
        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            //allow user to enter input into fields
            val username = binding.loginUsernameEditText.text.toString().trim()
            val password = binding.loginPasswordEditText.text.toString().trim()

            //handling -> if empty prompt to enter input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                //all details entered and valid -> log user in
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        //allow user to reset password
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.loginUsernameEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //link to reset password sent to email -> handled by firebase
                            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        //link to register screen
        binding.goToRegisterButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
