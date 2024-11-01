package com.first.googlelensclone

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextAnalysis : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    var textDone = "Recognising..."

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        Log.d("TEXT", "Image analysed")

        imageProxy.image?.let { mediaImage ->
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            recognizer.process(inputImage)
                .addOnSuccessListener { text ->
                    text.textBlocks.forEach { block ->
                        Log.d("TEXT", """
                            LINES = ${block.lines.joinToString("\n") { it.text }}
                            LANGUAGE = ${block.recognizedLanguage}
                        """.trimIndent())
                        textDone = block.text // Update recognized text
                    }
                }
                .addOnFailureListener { ex ->
                    Log.e("TEXT", "Detection failed", ex)
                }
                .addOnCompleteListener {
                    imageProxy.close() // Ensure the imageProxy is closed once processing is done
                }
        } ?: run {
            Log.e("TEXT", "Image not found, closing image proxy")
            imageProxy.close() // Close if the image is null
        }
    }
}
