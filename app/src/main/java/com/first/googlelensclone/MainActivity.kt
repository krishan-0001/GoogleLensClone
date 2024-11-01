package com.first.googlelensclone

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var button1: Button
    private lateinit var image: ImageView

    // Use ActivityResultLauncher instead of onActivityResult
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                image.setImageBitmap(imageBitmap)
            } else {
                Log.e("MainActivity", "Failed to capture image")
            }
        } else {
            Log.e("MainActivity", "Camera activity failed or cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        button = findViewById(R.id.btnTakePic)
        button1 = findViewById(R.id.btnML)
        image = findViewById(R.id.ivPic)

        // Set click listener for the camera button
        button.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                takePhotoLauncher.launch(takePhotoIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e("MainActivity", "Camera not found", e)
            }
        }

        // Set click listener for the machine learning options button
        button1.setOnClickListener {
            val mlIntent = Intent(this, OptionsActivity::class.java)
            startActivity(mlIntent)
        }
    }
}
