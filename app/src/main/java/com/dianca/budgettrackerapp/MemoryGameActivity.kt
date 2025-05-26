package com.dianca.budgettrackerapp


import android.os.Bundle
import android.os.Handler
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.R
import kotlin.random.Random

class MemoryGameActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private val cardImages = listOf(
        R.drawable.ic_coin, R.drawable.ic_wallet, R.drawable.ic_piggy_bank,
        R.drawable.ic_chart, R.drawable.ic_bank, R.drawable.ic_credit_card
    )
    private val cards = mutableListOf<Int>()
    private val cardViews = mutableListOf<ImageView>()
    private var firstCardIndex: Int? = null
    private var secondCardIndex: Int? = null
    private var isProcessing = false
    private var matchedPairs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_game)

        gridLayout = findViewById(R.id.gridLayout)

        prepareCards()
        setupGrid()
    }

    private fun prepareCards() {
        cards.clear()
        cards.addAll(cardImages)
        cards.addAll(cardImages) // duplicate for pairs
        cards.shuffle()
    }

    private fun setupGrid() {
        gridLayout.columnCount = 3
        gridLayout.rowCount = 4
        cardViews.clear()
        gridLayout.removeAllViews()

        for (i in 0 until cards.size) {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.card_back)
            imageView.setPadding(8, 8, 8, 8)
            imageView.adjustViewBounds = true
            imageView.setOnClickListener { onCardClicked(i) }
            cardViews.add(imageView)
            gridLayout.addView(imageView)
        }
    }

    private fun onCardClicked(index: Int) {
        if (isProcessing || index == firstCardIndex || cardViews[index].tag == "matched") return

        cardViews[index].setImageResource(cards[index])

        if (firstCardIndex == null) {
            firstCardIndex = index
        } else {
            secondCardIndex = index
            isProcessing = true

            Handler().postDelayed({
                checkMatch()
            }, 1000)
        }
    }

    private fun checkMatch() {
        val first = firstCardIndex!!
        val second = secondCardIndex!!

        if (cards[first] == cards[second]) {
            cardViews[first].tag = "matched"
            cardViews[second].tag = "matched"
            matchedPairs++
            if (matchedPairs == cardImages.size) {
                Toast.makeText(this, "🎉 You matched all cards!", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            cardViews[first].setImageResource(R.drawable.card_back)
            cardViews[second].setImageResource(R.drawable.card_back)
        }

        firstCardIndex = null
        secondCardIndex = null
        isProcessing = false
    }
}
