package com.first.googlelensclone

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelAnalyzer : ImageAnalysis.Analyzer {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.7F)
            .build()
    )

    private val _labelDone = MutableLiveData<String>("unknown")
    val labelDone: LiveData<String> get() = _labelDone

    private val _labelAcc = MutableLiveData<Float>(1.0f)
    val labelAcc: LiveData<Float> get() = _labelAcc

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        Log.d("ImageLabelAnalyzer", "Analyzing image")

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Passing image to ML Kit Vision API
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Task completed successfully
                    for (label in labels) {
                        Log.d(
                            "ImageLabelAnalyzer", """
                            Label: ${label.text}
                            Confidence: ${label.confidence}
                        """.trimIndent()
                        )
                        _labelDone.value = label.text
                        _labelAcc.value = label.confidence
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ImageLabelAnalyzer", "Detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close() // Always close the image proxy
                }
        } else {
            Log.e("ImageLabelAnalyzer", "Media image is null, closing ImageProxy")
            imageProxy.close() // Close if image is not found
        }
    }
}
