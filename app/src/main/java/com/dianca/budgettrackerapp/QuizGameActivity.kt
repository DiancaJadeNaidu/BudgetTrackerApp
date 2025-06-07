package com.dianca.budgettrackerapp

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.R

class QuizGameActivity : BaseActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button

    private val questions = listOf(
        Question("What is a budget?", listOf("A savings account", "A spending plan", "A type of loan", "A credit score"), 1),
        Question("Which of these is a liability?", listOf("Your car", "A student loan", "Your job", "A house you own"), 1),
        Question("What's a good reason to save money?", listOf("Emergency fund", "New shoes", "Expensive dinner", "Daily coffee"), 0),
        Question("Credit scores are used to:", listOf("Track income", "Determine loan eligibility", "Calculate taxes", "Pay rent"), 1),
        Question("What's compound interest?", listOf("Interest on interest", "Flat rate interest", "Negative balance", "Loan penalty"), 0)
    )

    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_game)

        //set up bottom navigation
        setupBottomNav()

        //initialise views using findViewById
        tvQuestion = findViewById(R.id.tvQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)

        loadQuestion()

        btnOption1.setOnClickListener { checkAnswer(0) }
        btnOption2.setOnClickListener { checkAnswer(1) }
        btnOption3.setOnClickListener { checkAnswer(2) }
        btnOption4.setOnClickListener { checkAnswer(3) }
    }

    private fun loadQuestion() {
        val q = questions[currentIndex]
        tvQuestion.text = q.text
        btnOption1.text = q.options[0]
        btnOption2.text = q.options[1]
        btnOption3.text = q.options[2]
        btnOption4.text = q.options[3]
    }

    private fun checkAnswer(selectedIndex: Int) {
        val correct = questions[currentIndex].correctAnswer
        if (selectedIndex == correct) {
            score++
        }

        currentIndex++
        if (currentIndex < questions.size) {
            loadQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        val advice = listOf(
            "Start saving 10% of your income today.",
            "Set financial goals and track progress weekly.",
            "Build an emergency fund with 3 months of expenses.",
            "Avoid high-interest debt as much as possible.",
            "Understand your credit report and monitor it regularly."
        ).random()

        AlertDialog.Builder(this)
            .setTitle("🎉 Quiz Complete!")
            .setMessage("You scored $score/${questions.size}.\n\n💡 Your financial tip:\n$advice")
            .setCancelable(false)
            .setPositiveButton("Back to Games") { _: DialogInterface, _: Int ->
                finish()
            }
            .show()
    }

    data class Question(val text: String, val options: List<String>, val correctAnswer: Int)
}
