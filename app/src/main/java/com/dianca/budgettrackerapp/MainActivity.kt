package com.dianca.budgettrackerapp

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.MotionEvent
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.dianca.budgettrackerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo: fade in and slide down
        binding.imageView.alpha = 0f
        binding.imageView.translationY = -100f
        binding.imageView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1500)
            .setInterpolator(BounceInterpolator())
            .start()

        // Animate Get Started button: pulse
        val pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
            binding.StartButton,
            PropertyValuesHolder.ofFloat("scaleX", 0.95f),
            PropertyValuesHolder.ofFloat("scaleY", 0.95f)
        )
        pulseAnimation.duration = 400
        pulseAnimation.repeatCount = ObjectAnimator.INFINITE
        pulseAnimation.repeatMode = ObjectAnimator.REVERSE
        pulseAnimation.interpolator = BounceInterpolator()
        pulseAnimation.start()

        // Touch event: spin logo on tap
        binding.imageView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                binding.imageView.animate().rotationBy(360f).setDuration(800).start()
                true
            } else {
                false
            }
        }

        // Button click: go to LoginActivity with optional vibration
        binding.StartButton.setOnClickListener {
            vibrateDevice()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun vibrateDevice() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }
}
