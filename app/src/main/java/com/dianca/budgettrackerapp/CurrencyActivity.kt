package com.dianca.budgettrackerapp

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class CurrencyActivity : AppCompatActivity() {
    var currencyListView: ListView? = null
    var saveCurrencyButton: Button? = null
    var currencies: Array<String> = arrayOf(
        "USD - US Dollar",
        "EUR - Euro",
        "GBP - British Pound",
        "INR - Indian Rupee",
        "AUD - Australian Dollar"
    )
    var currencyAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currency_activity)

        currencyListView = findViewById<ListView>(R.id.currency_list)
        saveCurrencyButton = findViewById<Button>(R.id.save_currency_button)

        currencyAdapter = ArrayAdapter(this, R.layout.simple_list_item_1, currencies)
        currencyListView.setAdapter(currencyAdapter)

        saveCurrencyButton.setOnClickListener(View.OnClickListener { v: View? -> })
    }
}
