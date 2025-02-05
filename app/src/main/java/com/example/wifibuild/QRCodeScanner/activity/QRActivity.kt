package com.example.wifibuild.QRCodeScanner.activity


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wifibuild.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*class QRActivity : AppCompatActivity() {

    private var isGenerating = false
    private var qrJob: Job? = null
    private lateinit var qrImageView: ImageView
    private lateinit var scannedResultTextView: TextView  // To display scanned QR result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)

        val generateQrButton: Button = findViewById(R.id.generateQrButton)
        val scanQrButton: Button = findViewById(R.id.scanQrButton)
        qrImageView = findViewById(R.id.qrImageView)
        scannedResultTextView = findViewById(R.id.scannedResultTextView)


        generateQrButton.setOnClickListener {
            isGenerating = true
            startGeneratingQR()
        }

        scanQrButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun startGeneratingQR() {
        qrJob = CoroutineScope(Dispatchers.Main).launch {
            while (isGenerating) {
                val dynamicMessage = "ParshuramBehera"
                generateQRCode(dynamicMessage, qrImageView)
                delay(3000)
            }
        }
    }

    private fun stopGeneratingQR() {
        isGenerating = false
        qrJob?.cancel()
    }

    private fun generateQRCode(message: String, imageView: ImageView) {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L
        )
        val bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, 512, 512, hints)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        imageView.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val scannedData = data?.getStringExtra("scanned_result")
            if (scannedData != null) {
                scannedResultTextView.text = "Scanned QR Code: $scannedData"
            } else {
                scannedResultTextView.text = "Scan failed. Try again."
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGeneratingQR()  // Stop coroutine when the activity is destroyed
    }
}*/

class QRActivity : AppCompatActivity() {
    private var isGenerating = false  // Flag to track QR generation status
    private var qrJob: Job? = null
    private lateinit var qrImageView: ImageView
    private lateinit var scannedResultTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qractivity)


        val generateQrButton: Button = findViewById(R.id.generateQrButton)
        val scanQrButton: Button = findViewById(R.id.scanQrButton)
        qrImageView = findViewById(R.id.qrImageView)
        scannedResultTextView = findViewById(R.id.scannedResultTextView)


        generateQrButton.setOnClickListener {
            /* val customMessage = "1234"  // Customize your message
             generateQRCode(customMessage, qrImageView)*/

            isGenerating = true
            startGeneratingQR()
        }

        scanQrButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivityForResult(intent, 1)
        }

    }

    private fun startGeneratingQR() {
        qrJob = CoroutineScope(Dispatchers.Main).launch {
            while (isGenerating) {
                val dynamicMessage = "ParshuramBehera"

                // can add this to message to make it dynamically changing QR
                /*+ "\u200B".repeat((0..5).random())*/  // Dynamic message with timestamp
                generateQRCode(dynamicMessage, qrImageView)

                //changing frequency
                delay(2000)
            }
        }
    }

    private fun stopGeneratingQR() {
        isGenerating = false
        qrJob?.cancel()
    }

    private fun generateQRCode(message: String, imageView: ImageView) {

        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.entries.toTypedArray()
            .random()
        )
        val bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, 512, 512, hints)


        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        imageView.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val scannedData = data?.getStringExtra("scanned_result")
            if (scannedData != null) {
                scannedResultTextView.text = "Scanned QR Code: $scannedData"
            } else {
                scannedResultTextView.text = "Scan failed. Try again."
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGeneratingQR()  // Stop coroutine when the activity is destroyed
    }
}
