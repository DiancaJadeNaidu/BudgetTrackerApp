package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.HomeActivity
import com.dianca.budgettrackerapp.R
import com.dianca.budgettrackerapp.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        usernameInput = findViewById(R.id.edtUsername)
        passwordInput = findViewById(R.id.edtPassword)
        loginButton = findViewById(R.id.btnLogin)
        registerText = findViewById(R.id.txtRegister)

        registerText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Commented out the coroutine logic since the database part isn't yet implemented
            // CoroutineScope(Dispatchers.IO).launch {
            //     val user = db.userDao().getUser(username, password)

            //     withContext(Dispatchers.Main) {  //commented out the database result handling
            //         if (user != null) {
            Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
            finish()
            //         } else {
            //             Toast.makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            //         }
            //     }
            // }
        }
    }
}
