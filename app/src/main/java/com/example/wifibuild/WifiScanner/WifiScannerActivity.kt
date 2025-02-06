package com.example.wifibuild.WifiScanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.wifibuild.R
import com.example.wifibuild.permissionManager.MyBaseActivity
import com.example.wifibuild.permissionManager.MyPermissionManager

class WifiScannerActivity : MyBaseActivity() {

    private lateinit var buttonText: TextView
    private lateinit var progressBar:ProgressBar
    private lateinit var listenButton: LinearLayout
    private lateinit var wifiListView: ListView
    private lateinit var wifiManager: WifiManager
    private lateinit var adapter: ArrayAdapter<String>
    private var wifiList = mutableListOf<String>()

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                if (ActivityCompat.checkSelfPermission(
                        this@WifiScannerActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    MyPermissionManager.getInstance().requestPermission(
                        this@WifiScannerActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        101
                    )
                    return
                }

                val results = wifiManager.scanResults ?: emptyList()
                wifiList.clear()

                for (result in results) {
                    if (result.SSID.isNotEmpty()) { // Filter empty SSIDs
                        wifiList.add("${result.SSID} - Signal: ${result.level} dBm")
                    }
                }

                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
                buttonText.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scanner)

        buttonText = findViewById(R.id.textScan)
        progressBar = findViewById(R.id.scanProgress)
        listenButton = findViewById(R.id.receiveListener)
        wifiListView = findViewById(R.id.listView)

        progressBar.visibility = View.GONE

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        MyPermissionManager.getInstance()
            .requestPermission(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, wifiList)
        wifiListView.adapter = adapter

        listenButton.setOnClickListener {
            startWifiScan()
            buttonText.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun startWifiScan() {
        if (wifiManager.isWifiEnabled) {
            if (!isLocationEnabled()) {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
                return
            }

            registerReceiver(
                wifiScanReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION),
                RECEIVER_EXPORTED
            )
            wifiManager.startScan()
        } else {
            Toast.makeText(this, "Please enable Wi-Fi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(wifiScanReceiver)
            buttonText.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}

