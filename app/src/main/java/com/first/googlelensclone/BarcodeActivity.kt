package com.first.googlelensclone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private val imageAnalyzer: ImageAnalysis.Analyzer = ImageLabelAnalyzer()
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var btnLabelScanner: Button
    private lateinit var textView: TextView
    private lateinit var textView1: TextView
    private lateinit var viewFinder: PreviewView
    private var labelDone: String? = null
    private var labelAcc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)

        // Initialize views
        btnLabelScanner = findViewById(R.id.btnLabelScaner)
        textView = findViewById(R.id.tvLabel)
        textView1 = findViewById(R.id.tvLabelAcc)
        viewFinder = findViewById(R.id.viewFinder)

        // Set up Image Analysis
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        btnLabelScanner.setOnClickListener {
            startImageLabeling()
        }

        // Request camera permissions
        askCameraPermission()

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }

    companion object {
        private const val TAG = "BarcodeActivity"
        private const val CAMERA_PERM_CODE = 422
    }

    private fun askCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CAMERA_PERM_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                showPermissionErrorDialog()
            }
        }
    }

    private fun showPermissionErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Error")
            .setMessage("Camera Permission not provided")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind previous use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the use cases to the camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startImageLabeling() {
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            imageAnalyzer
        )

        textView.text = labelDone?.toString() ?: "No label available"
        textView1.text = labelAcc?.toString() ?: "No accuracy data"
    }
}
