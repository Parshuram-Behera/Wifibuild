package com.example.wifibuild

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wifibuild.permissionManager.MyBaseActivity
import com.example.wifibuild.permissionManager.MyPermissionManager

class WifiP2PActivity : MyBaseActivity() {

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    val peers = mutableListOf<WifiP2pDevice>()

    private lateinit var sendButton: LinearLayout
    private lateinit var receiveButton: LinearLayout
    private lateinit var recyclerView: RecyclerView
   // lateinit var adapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize Wi-Fi P2P manager and channel
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        // Set up intent filter for Wi-Fi P2P actions
        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        // Initialize UI elements
      //  sendButton = findViewById(R.id.circle_send)
       // receiveButton = findViewById(R.id.circle_receive)
        //recyclerView = findViewById(R.id.rv_devices)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter and set it to RecyclerView
       // adapter = DeviceAdapter(peers) { device ->
         //   connectToDevice(device)
       // }
       // recyclerView.adapter = adapter

        // Set up button click listeners
        receiveButton.setOnClickListener {
            discoverPeers()
        }
    }

    private fun discoverPeers() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            MyPermissionManager.getInstance().requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                101
            )
            Toast.makeText(this@WifiP2PActivity, "Ask Permission Discovery", Toast.LENGTH_SHORT)
                .show()
        } else {
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(this@WifiP2PActivity, "Discovery started", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        this@WifiP2PActivity,
                        "Discovery failed: $reason",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            MyPermissionManager.getInstance().requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                101
            )
        } else {
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Toast.makeText(this@WifiP2PActivity, "Connection initiated", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        this@WifiP2PActivity,
                        "Connection failed: $reason",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
      //  receiver = WifiP2PReceiver(wifiP2pManager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}
