package com.dianca.budgettrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate the layout and set it as the content view
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up bottom navigation bar
        setupBottomNav()

        //load HomeFragment on first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, HomeFragment())
                .commit()
        }
    }
}
