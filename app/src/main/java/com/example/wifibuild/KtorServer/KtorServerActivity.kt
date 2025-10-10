package com.example.wifibuild.KtorServer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.wifibuild.R

class KtorServerActivity : AppCompatActivity() {

    private lateinit var startBtn: Button


    private val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }.toTypedArray()

    @RequiresApi(Build.VERSION_CODES.O)
    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.all { it.value }
        if (allGranted) {
            startKinflowServer()
        } else {
            Toast.makeText(this, "All permissions required!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ktor_server)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        startBtn = findViewById(R.id.start_btn)


        startBtn.setOnClickListener {

            requestPermissionsAndStart()

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestPermissionsAndStart() {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            startKinflowServer()
        } else {
            permissionRequestLauncher.launch(permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startKinflowServer() {
        val viewModel = ViewModelProvider(this)[KtorServerViewModel::class.java]
        viewModel.startKinFlowBroadcast(this)
    }
}