package com.example.wifidirect

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class WifiPeerAdapter(context: Context, private val devices: List<WifiP2pDevice>) :
    ArrayAdapter<WifiP2pDevice>(context, android.R.layout.simple_list_item_2, devices) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val device = devices[position]
        val nameTextView = view.findViewById<TextView>(android.R.id.text1)
        val addressTextView = view.findViewById<TextView>(android.R.id.text2)

        nameTextView.text = device.deviceName
        addressTextView.text = device.deviceAddress

        return view
    }
}
