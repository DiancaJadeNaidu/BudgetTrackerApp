package com.dianca.budgettrackerapp


import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.R
import kotlin.random.Random

class TapGameActivity : BaseActivity() {

    private lateinit var gameLayout: FrameLayout
    private lateinit var tapTarget: ImageView
    private lateinit var adviceText: TextView
    private val handler = Handler()
    private val adviceList = listOf(
        "💡 Save 10% of every paycheck.",
        "💳 Avoid impulse buying.",
        "📊 Create and stick to a budget.",
        "🏦 Build an emergency fund.",
        "💰 Invest early for compound growth.",
        "🔁 Automate your savings.",
        "🧾 Track your monthly expenses.",
        "📈 Learn the power of interest.",
        "📉 Avoid lifestyle inflation.",
        "🛍️ Differentiate needs vs wants."
    )

    private val moveTargetRunnable = object : Runnable {
        override fun run() {
            moveTapTarget()
            handler.postDelayed(this, 1500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_game)

        //set up bottom navigation
        setupBottomNav()

        gameLayout = findViewById(R.id.tapGameLayout)
        tapTarget = findViewById(R.id.tapTarget)
        adviceText = findViewById(R.id.adviceText)

        tapTarget.setOnClickListener {
            showRandomAdvice()
            moveTapTarget()
        }

        handler.post(moveTargetRunnable)
    }

    private fun showRandomAdvice() {
        val advice = adviceList.random()
        adviceText.text = advice
    }

    private fun moveTapTarget() {
        val parentWidth = gameLayout.width - tapTarget.width
        val parentHeight = gameLayout.height - tapTarget.height

        if (parentWidth > 0 && parentHeight > 0) {
            val newX = Random.nextInt(0, parentWidth)
            val newY = Random.nextInt(0, parentHeight)

            tapTarget.animate().x(newX.toFloat()).y(newY.toFloat()).setDuration(300).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(moveTargetRunnable)
    }
}
