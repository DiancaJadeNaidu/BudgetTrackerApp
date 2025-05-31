package com.dianca.budgettrackerapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityPayfastCheckoutBinding
import com.dianca.budgettrackerapp.utils.DailyRewardManager

class PayfastCheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityPayfastCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayfastCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up bottom navigation
        setupBottomNav()

        binding.payButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val card = binding.cardInput.text.toString()
            val bank = binding.bankInput.text.toString()

            if (name.isNotEmpty() && card.length >= 8 && bank.isNotEmpty()) {
                DailyRewardManager.resetBalance(this)
                Toast.makeText(this, "✅ Payment successful! R500 will be sent to your account.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "❌ Please complete all fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }
}
