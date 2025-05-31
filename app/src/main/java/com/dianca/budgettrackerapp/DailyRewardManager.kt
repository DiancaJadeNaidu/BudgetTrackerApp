package com.dianca.budgettrackerapp.utils

import android.content.Context

object DailyRewardManager {
    private const val PREF_NAME = "daily_rewards"
    private const val KEY_LAST_CLAIM = "last_claim"
    private const val KEY_BALANCE = "balance"

    fun isRewardAvailable(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastClaimTime = prefs.getLong(KEY_LAST_CLAIM, 0)
        val now = System.currentTimeMillis()
        return now - lastClaimTime > 24 * 60 * 60 * 1000 // 24 hours
    }

    fun resetBalance(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_BALANCE, 0).apply()
    }

    fun claimReward(context: Context, amount: Int = 500): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastClaimTime = prefs.getLong(KEY_LAST_CLAIM, 0)
        val now = System.currentTimeMillis()

        val elapsed = now - lastClaimTime
        val oneDayMillis = 24 * 60 * 60 * 1000L

        if (elapsed < oneDayMillis) {
            return false
        }

        val daysPassed = (elapsed / oneDayMillis).toInt()
        val totalReward = amount * daysPassed

        val currentBalance = prefs.getInt(KEY_BALANCE, 0)
        prefs.edit()
            .putInt(KEY_BALANCE, currentBalance + totalReward)
            .putLong(KEY_LAST_CLAIM, now)
            .apply()
        return true
    }

    fun getBalance(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_BALANCE, 0)
    }
}
