package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityRewardBinding
import com.dianca.budgettrackerapp.utils.DailyRewardManager

class RewardActivity : BaseActivity() {
    private lateinit var binding: ActivityRewardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up bottom navigation
        setupBottomNav()

        updateRewardMessage()

        binding.claimButton.setOnClickListener {
            val claimed = DailyRewardManager.claimReward(this)
            if (claimed) {
                Toast.makeText(this, "✅ You claimed R500!", Toast.LENGTH_SHORT).show()
                binding.checkoutButton.visibility = android.view.View.VISIBLE
                binding.claimButton.isEnabled = false
            } else {
                Toast.makeText(this, "⚠️ You've already claimed your reward today.", Toast.LENGTH_SHORT).show()
            }
            updateRewardMessage()
        }

        binding.checkoutButton.setOnClickListener {
            val balance = DailyRewardManager.getBalance(this)
            if (balance > 0) {
                DailyRewardManager.resetBalance(this) // Reset after checkout
                startActivity(Intent(this, PayfastCheckoutActivity::class.java))
            } else {
                Toast.makeText(this, "❌ No rewards to checkout. Please claim first.", Toast.LENGTH_SHORT).show()
            }
            updateRewardMessage()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateRewardMessage()
    }

    private fun updateRewardMessage() {
        val isAvailable = DailyRewardManager.isRewardAvailable(this)
        val balance = DailyRewardManager.getBalance(this)

        binding.rewardBalance.text = "Your balance: R$balance"

        if (isAvailable) {
            binding.rewardMessage.text = "🎁 Daily reward is available!"
            binding.claimButton.visibility = android.view.View.VISIBLE
            binding.checkoutButton.visibility = android.view.View.GONE
        } else {
            binding.rewardMessage.text = "❌ Already claimed today."
            binding.claimButton.visibility = android.view.View.GONE
            binding.checkoutButton.visibility = if (balance > 0) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

}

