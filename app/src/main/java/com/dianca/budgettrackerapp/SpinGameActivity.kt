package com.dianca.budgettrackerapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.R
import kotlin.random.Random

class SpinGameActivity : AppCompatActivity() {

    private lateinit var spinButton: Button
    private lateinit var wheelImage: ImageView
    private lateinit var adviceText: TextView

    private val adviceList = listOf(
        "💳 Pay your credit card balance in full.",
        "📈 Invest in a diversified portfolio.",
        "🧾 Track your monthly expenses.",
        "🏦 Build your emergency fund.",
        "💰 Start saving for retirement early.",
        "📊 Make a monthly budget.",
        "⚠️ Avoid high-interest debt.",
        "💡 Set financial goals.",
        "🛒 Don’t shop when you’re emotional.",
        "🔐 Always save before spending."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_game)

        spinButton = findViewById(R.id.spinButton)
        wheelImage = findViewById(R.id.wheelImage)
        adviceText = findViewById(R.id.adviceText)

        spinButton.setOnClickListener {
            spinWheel()
        }
    }

    private fun spinWheel() {
        val rotation = (360..1440).random()
        val animator = ObjectAnimator.ofFloat(wheelImage, View.ROTATION, 0f, rotation.toFloat())
        animator.duration = 2000
        animator.start()

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val randomAdvice = adviceList.random()
                adviceText.text = randomAdvice
            }
        })
    }
}
