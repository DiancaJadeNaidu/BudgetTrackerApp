package com.dianca.budgettrackerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityProfileBinding

class ProfileActivity : BaseActivity() {

    //view binding for accessing views in activity_profile.xml
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //inflate layout using ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up bottom navigation bar
        setupBottomNav()

        //load ProfileFragment on first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, ProfileFragment())
                .commit()
        }
    }
}
