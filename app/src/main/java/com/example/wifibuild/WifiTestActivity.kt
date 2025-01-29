package com.example.wifibuild

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wifibuild.permissionManager.MyPermissionManager
import com.example.wifidirect.WiFiDirectBroadcastReceiver
import com.example.wifidirect.WifiPeerAdapter
import java.util.logging.Handler

class WifiTestActivity : AppCompatActivity() {
    private lateinit var discoverButton: Button
    private lateinit var refreshButton: Button
    private lateinit var listView: ListView
    private lateinit var connectionStatusTextView: TextView  // Added to show connection status
    private val handler = android.os.Handler(Looper.getMainLooper())

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    private var peers = mutableListOf<WifiP2pDevice>()
    private lateinit var adapter: WifiPeerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_test)

        discoverButton = findViewById(R.id.btnDiscover)
        refreshButton = findViewById(R.id.btnDiscover)
        connectionStatusTextView = findViewById(R.id.txtConnectionStatus)  // UI element for status
        listView = findViewById(R.id.peerListView)

        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        receiver = WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this)
        adapter = WifiPeerAdapter(this, peers)
        listView.adapter = adapter

        discoverButton.setOnClickListener {
            discoverPeersWithTimeout()
        }

        refreshButton.setOnClickListener {
            discoverPeersWithTimeout()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            connectToPeer(peers[position])
        }

        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    fun updateConnectionStatus(status: String) {
        runOnUiThread {
            connectionStatusTextView.text = status
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val missingPermissions = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 101)
        }
    }

    private fun discoverPeersWithTimeout() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {

            MyPermissionManager.getInstance().requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                102
            )
            return
        }

        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@WifiTestActivity, "Peer discovery started", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@WifiTestActivity, "Peer discovery failed: $reason", Toast.LENGTH_SHORT).show()
            }
        })

        handler.postDelayed({ stopPeerDiscovery() }, 10_000)  // Stop after 10 sec
    }

    private fun stopPeerDiscovery() {
        wifiP2pManager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@WifiTestActivity, "Peer discovery stopped", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@WifiTestActivity, "Failed to stop discovery: $reason", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updatePeerList(peerList: Collection<WifiP2pDevice>) {
        peers.clear()
        peers.addAll(peerList)
        adapter.notifyDataSetChanged()
    }
    fun showConnectionRequest(device: WifiP2pDevice) {
        AlertDialog.Builder(this)
            .setTitle("Connection Request")
            .setMessage("Accept connection from ${device.deviceName}?")
            .setPositiveButton("Accept") { _, _ ->
                acceptConnection(device)
            }
            .setNegativeButton("Reject") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun acceptConnection(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            MyPermissionManager.getInstance().requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                102
            )
            return
        }

        wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(
                    this@WifiTestActivity, "Connected to ${device.deviceName}", Toast.LENGTH_SHORT
                ).show()
                updateConnectionStatus("Connected to ${device.deviceName}")
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@WifiTestActivity, "Connection failed: $reason", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun connectToPeer(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {

            MyPermissionManager.getInstance().requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                102
            )
            return
        }

        wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@WifiTestActivity, "Connecting to ${device.deviceName}...", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@WifiTestActivity, "Connection failed: $reason", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

