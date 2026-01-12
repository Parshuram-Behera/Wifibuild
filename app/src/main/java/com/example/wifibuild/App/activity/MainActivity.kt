package com.example.wifibuild.App.activity

import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.wifibuild.R

class MainActivity : AppCompatActivity() {

    private lateinit var payBtn: Button
    private lateinit var amountField: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // signInBtn = findViewById(R.id.btn_sign)
        payBtn = findViewById(R.id.pay)
        amountField = findViewById(R.id.et_amount)


        payBtn.setOnClickListener {

            if (amountField.text.isNotEmpty()) {
                val amount = amountField.text.toString().trim()

                setUPIDeepLinking(amount)
            }

        }


        /* signInBtn.setOnClickListener {

             */

        /**
        Here I Am Constructing the Github CallBack DeepLinking URL
         *//*

            val signUri = ("https://github.com/login/oauth/authorize" +
                    "?client_id=$CLIENT_ID" +
                    "&redirect_uri=wifibuild://auth/callback").toUri()

            startActivity(Intent(Intent.ACTION_VIEW, signUri))

            finish()
        }*/

    }

    private fun setUPIDeepLinking(amount: String) {


        val upiUri =  Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "7735274351@kotak")
                .appendQueryParameter("pn", "Parshuram")
                .appendQueryParameter("tr", "wifi45601")
                .appendQueryParameter("tn", "Test Transaction")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build()


        val intent = Intent(Intent.ACTION_VIEW).apply {5223

            data = upiUri
            setPackage("com.phonepe.app")
        }


        // Try PhonePe first, fallback to chooser if not installed
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 101)
        } else {
            // PhonePe not installed, use any UPI app
            val chooser = Intent.createChooser(Intent(Intent.ACTION_VIEW, upiUri), "Pay with UPI")
            startActivityForResult(chooser, 101)
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)


        if (requestCode == 101) {
            val response = data?.getStringExtra("response") ?: "No Response"
            Log.d("UPITest", "UPI Payment Response: $response")

            if (resultCode == RESULT_OK || resultCode == 11) {
                // Check response content
                if (response.contains("Status=SUCCESS", ignoreCase = true)) {
                    Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Payment Cancelled or Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}