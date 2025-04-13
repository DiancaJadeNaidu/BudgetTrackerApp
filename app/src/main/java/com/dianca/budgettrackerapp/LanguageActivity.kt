package com.dianca.budgettrackerapp
//
//import android.os.Bundle
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.ListView
//import androidx.appcompat.app.AppCompatActivity
//
//class LanguageActivity : AppCompatActivity() {
//    private var languageListView: ListView? = null
//    private var saveLanguageButton: Button? = null
//    private var languages: Array<String> = arrayOf("English", "Spanish", "French", "German", "Chinese", "Japanese")
//    private var languageAdapter: ArrayAdapter<String>? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.language_activity)
//
//        languageListView = findViewById(R.id.language_list)
//        saveLanguageButton = findViewById(R.id.save_language_button)
//
//        languageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
//        languageListView?.adapter = languageAdapter
//
//        saveLanguageButton?.setOnClickListener { view: View? ->
//        }
//    }
//}
