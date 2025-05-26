package com.dianca.budgettrackerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.dianca.budgettrackerapp.R

class MiniGamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mini_games)

        findViewById<CardView>(R.id.cardQuiz).setOnClickListener {
            startActivity(Intent(this, QuizGameActivity::class.java))
        }

        findViewById<CardView>(R.id.cardMemory).setOnClickListener {
            startActivity(Intent(this, MemoryGameActivity::class.java))
        }

        findViewById<CardView>(R.id.cardTapGame).setOnClickListener {
            startActivity(Intent(this, TapGameActivity::class.java))
        }

        findViewById<CardView>(R.id.cardTriviaSpin).setOnClickListener {
            startActivity(Intent(this, SpinGameActivity::class.java))
        }
    }
}
