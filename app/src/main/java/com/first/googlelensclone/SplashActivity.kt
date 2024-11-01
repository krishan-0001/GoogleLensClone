package com.first.googlelensclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Duration for the splash screen
        val splashDuration: Long = 2000

        // Using Handler to post a delayed Runnable
        Handler(Looper.getMainLooper()).postDelayed({
            // Start MainActivity after the delay
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close SplashActivity
        }, splashDuration)
    }
}
