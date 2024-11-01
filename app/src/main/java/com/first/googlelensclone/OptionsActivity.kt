package com.first.googlelensclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OptionsActivity : AppCompatActivity() {
    private lateinit var btnLabeler: Button
    private lateinit var btnTextRecognition: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        // Initialize buttons
        btnLabeler = findViewById(R.id.btnLabeler)
        btnTextRecognition = findViewById(R.id.btnTextR)

        // Set click listener for the labeler button (Barcode Activity)
        btnLabeler.setOnClickListener {
            startActivity(Intent(this, BarcodeActivity::class.java))
        }

        // Set click listener for the text recognition button
        btnTextRecognition.setOnClickListener {
            startActivity(Intent(this, TextRecognitionActivity::class.java))
        }
    }
}
