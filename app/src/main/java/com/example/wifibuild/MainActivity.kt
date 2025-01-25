package com.example.wifibuild

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.wifibuild.viewModel.FileTransferClient
import com.example.wifibuild.viewModel.FileTransferServer
import com.example.wifibuild.viewModel.MainViewModel
import com.example.wifibuild.viewModel.WebSocketHostServer
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class MainActivity : AppCompatActivity() {


    lateinit var sendButton: LinearLayout
    lateinit var receiveButton: LinearLayout
    lateinit var animationLayout: LinearLayout
    lateinit var animationView: LottieAnimationView

    val viewModel by lazy { MainViewModel(Application()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendButton = findViewById(R.id.circle_send)
        receiveButton = findViewById(R.id.circle_receive)
        animationLayout = findViewById(R.id.animation_layout)
        animationView = findViewById(R.id.lottieAnimationView)

        findViewById<LinearLayout>(R.id.circle_send).setOnClickListener {

        }

        // Connect to Host Button
        findViewById<LinearLayout>(R.id.circle_receive).setOnClickListener {

        }

        // Send File Button
      /*  findViewById<Button>(R.id.btnSendFile).setOnClickListener {
            val filePath = "/path/to/your/file.txt" // Replace with actual file path
            val hostAddress = "192.168.4.1" // Replace with actual host IP
            sendFileToHost(hostAddress, filePath)
            Toast.makeText(this, "Sending file to host", Toast.LENGTH_SHORT).show()
        }*/



        sendButton.setOnClickListener {
            animationLayout.visibility = View.VISIBLE
            startWebSocketServer()
            startFileServer()
            Toast.makeText(this, "Hosting started", Toast.LENGTH_SHORT).show()


            receiveButton.visibility = View.GONE
            animationView.setAnimation(R.raw.send_lottie_json)
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.playAnimation()


        }

        receiveButton.setOnClickListener {
            animationLayout.visibility = View.VISIBLE
            sendButton.visibility = View.GONE

            val hostAddress = "192.168.4.1" // Replace with actual host IP
            connectToHost(hostAddress)
            Toast.makeText(this, "Connecting to host", Toast.LENGTH_SHORT).show()
            animationView.setAnimation(R.raw.receive_lottie_json)
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.playAnimation()
        }
    }

    private fun startWebSocketServer() {
        val server = WebSocketHostServer(1024)
        server.start()
    }

    private fun connectToHost(hostAddress: String) {
        val client = object : WebSocketClient(URI("ws://$hostAddress:8888")) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                println("Connected to server")
                send("Hello from client!")
            }

            override fun onMessage(message: String?) {
                println("Message from server: $message")
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                println("Disconnected from server: $reason")
            }

            override fun onError(ex: Exception?) {
                println("Error: ${ex?.message}")
            }
        }
        client.connect()
    }

    private fun startFileServer() {
        val savePath = filesDir.absolutePath + "/received_file.txt"
        FileTransferServer.startFileServer(8989, savePath)
    }

    private fun sendFileToHost(hostAddress: String, filePath: String) {
        FileTransferClient.sendFileToHost(hostAddress, 8989, filePath)
    }
}