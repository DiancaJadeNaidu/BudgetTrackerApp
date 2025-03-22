package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    // private lateinit var db: AppDatabase  //commented out the database initialization
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // db = AppDatabase.getDatabase(this)  //commented out the database instantiation

        usernameInput = findViewById(R.id.edtUsername)
        passwordInput = findViewById(R.id.edtPassword)
        confirmPasswordInput = findViewById(R.id.edtConfirmPassword)
        registerButton = findViewById(R.id.btnRegister)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //commenting out the database check and insertion logic
            // CoroutineScope(Dispatchers.IO).launch {
            //     val existingUser = db.userDao().getUserByUsername(username)
            //     if (existingUser != null) {
            //         withContext(Dispatchers.Main) {
            //             Toast.makeText(this@RegisterActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
            //         }
            //         return@launch
            //     }

            //     db.userDao().insertUser(UserEntity(username = username, password = password))

            //     withContext(Dispatchers.Main) {
            //         Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
            //         val intent = Intent(this@RegisterActivity, EventCreationActivity::class.java)
            //         intent.putExtra("USERNAME", username)
            //         startActivity(intent)
            //         finish()
            //     }
            // }

            //for now, just to show a successful registration
            Toast.makeText(this@RegisterActivity, "Registration Successful!", Toast.LENGTH_SHORT).show()
       //     val intent = Intent(this@RegisterActivity, EventCreationActivity::class.java)
       //     intent.putExtra("USERNAME", username)
        //    startActivity(intent)
        //    finish()
        }
    }
}
