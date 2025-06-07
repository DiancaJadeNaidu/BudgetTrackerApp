package com.dianca.budgettrackerapp.utils

import android.content.Context

object DailyRewardManager {
    private const val PREF_NAME = "daily_rewards"
    private const val KEY_LAST_CLAIM = "last_claim"
    private const val KEY_BALANCE = "balance"
    private const val REWARD_AMOUNT = 500

    fun isRewardAvailable(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastClaimTime = prefs.getLong(KEY_LAST_CLAIM, 0)
        val now = System.currentTimeMillis()
        return now - lastClaimTime > 24 * 60 * 60 * 1000 //24 hours
    }

    fun claimReward(context: Context): Boolean {
        if (!isRewardAvailable(context)) return false

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_BALANCE, REWARD_AMOUNT)
            .putLong(KEY_LAST_CLAIM, System.currentTimeMillis())
            .apply()
        return true
    }

    fun getBalance(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_BALANCE, 0)
    }

    fun resetBalance(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_BALANCE, 0).apply()
    }
}
