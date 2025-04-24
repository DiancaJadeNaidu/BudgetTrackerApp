package com.dianca.budgettrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    //view binding instance for the layout
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate the layout using ViewBinding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up bottom navigation bar
        setupBottomNav()

        //load SettingsFragment on first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, SettingsFragment())
                .commit()
        }
    }
}
