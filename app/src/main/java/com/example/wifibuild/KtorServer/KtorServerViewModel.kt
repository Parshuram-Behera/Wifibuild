package com.example.wifibuild.KtorServer


import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.wifibuild.KtorServer.Constants.APP_IDENTIFIER
import com.example.wifibuild.KtorServer.Constants.BROADCAST_PORT
import com.example.wifibuild.KtorServer.Constants.WEBSOCKET_PORT
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.time.Duration


/*class KtorServerViewModel(application: Application) : AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun startHotspot(context: Context, onReady: (String) -> Unit) {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasNearbyPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!hasFineLocation || !hasNearbyPermission) {
            Log.d("KtorServerVM", "Missing permissions: FineLocation=$hasFineLocation, Nearby=$hasNearbyPermission")
            return
        }

        Log.d("KtorServerVM", "Starting LocalOnlyHotspot...")

        wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                Log.d("KtorServerVM", "Hotspot started: SSID=${reservation?.wifiConfiguration?.SSID}, Password=${reservation?.wifiConfiguration?.preSharedKey}")
                val ip = "192.168.43.1" // Usually the gateway IP of hotspot
                onReady(ip)
            }

            override fun onFailed(reason: Int) {
                Log.e("KtorServerVM", "Hotspot failed to start, reason code: $reason")
            }
        }, Handler(Looper.getMainLooper()))
    }

    fun broadcastIp(ip: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = DatagramSocket()
                socket.broadcast = true
                val message = "$APP_IDENTIFIER|$ip:$WEBSOCKET_PORT"
                val packet = DatagramPacket(
                    message.toByteArray(),
                    message.length,
                    InetAddress.getByName("255.255.255.255"),
                    BROADCAST_PORT
                )
                Log.d("KtorServerVM", "Broadcasting IP: $message")

                while (true) {
                    socket.send(packet)
                    Log.d("KtorServerVM", "Broadcast packet sent.")
                    delay(3000)
                }
            } catch (e: Exception) {
                Log.e("KtorServerVM", "Error broadcasting IP: ${e.localizedMessage}", e)
            }
        }
    }

    fun startWebSocketServer() {
        Log.d("KtorServerVM", "Starting WebSocket server...")

        val server = embeddedServer(Netty, port = WEBSOCKET_PORT, host = "0.0.0.0") {
            install(WebSockets)

            routing {
                webSocket("/receiver") {
                    Log.d("KtorServerVM", "New WebSocket connection from: ${call.request.local.remoteHost}")
                    val init = incoming.receive()
                    if (init is Frame.Text) {
                        val text = init.readText()
                        Log.d("KtorServerVM", "Received handshake: $text")
                        if (text == APP_IDENTIFIER) {
                            send(Frame.Text("Authenticated"))
                            Log.d("KtorServerVM", "Client authenticated.")
                            for (frame in incoming) {
                                if (frame is Frame.Text) {
                                    Log.d("KtorServerVM", "Received from client: ${frame.readText()}")
                                }
                            }
                        } else {
                            Log.w("KtorServerVM", "Client failed authentication: $text")
                            close(
                                CloseReason(
                                    CloseReason.Codes.VIOLATED_POLICY,
                                    "Invalid handshake"
                                )
                            )
                        }
                    }
                }
            }
        }

        server.start(wait = false)
        Log.d("KtorServerVM", "WebSocket server started on port $WEBSOCKET_PORT")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startKinFlowBroadcast(context: Context) {
        Log.d("KtorServerVM", "Initializing KinFlow Broadcast...")
        startHotspot(context) { ip ->
            Log.d("KtorServerVM", "Hotspot ready with IP: $ip")
            startWebSocketServer()
            broadcastIp(ip)
        }
    }
}*/

class KtorServerViewModel(application: Application) : AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun startKinFlowBroadcast(context: Context) {
        startHotspot(context) { ip ->
            startWebSocketServer()
            broadcastIp(ip)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startHotspot(context: Context, onReady: (String) -> Unit) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasNearbyPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else true

        if (!hasFineLocation || !hasNearbyPermission) return

        wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                File("shared_files").mkdirs()
                onReady("192.168.43.1")
            }
        }, Handler(Looper.getMainLooper()))
    }

    fun broadcastIp(ip: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val socket = DatagramSocket()
            socket.broadcast = true
            val message = "$APP_IDENTIFIER|$ip:$WEBSOCKET_PORT"
            val packet = DatagramPacket(
                message.toByteArray(),
                message.length,
                InetAddress.getByName("255.255.255.255"),
                BROADCAST_PORT
            )
            while (true) {
                socket.send(packet)
                delay(3000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startWebSocketServer() {
        val server = embeddedServer(Netty, port = WEBSOCKET_PORT, host = "0.0.0.0") {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
            }
            routing {
                webSocket("/receiver") {
                    val init = incoming.receive()
                    if (init is Frame.Text && init.readText().startsWith(APP_IDENTIFIER)) {
                        send(Frame.Text("Authenticated"))
                        send(Frame.Text("FILE_AVAILABLE|sample.txt|2KB"))
                        for (frame in incoming) {
                            if (frame is Frame.Text) println("Received: ${frame.readText()}")
                        }
                    } else close(
                        CloseReason(
                            CloseReason.Codes.VIOLATED_POLICY, "Invalid handshake"
                        )
                    )
                }

                static("/files") {

                    files("shared_files")
                }

                post("/upload") {
                    val multipart = call.receiveMultipart()
                    var fileName = "unknown"
                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            fileName =
                                part.originalFileName ?: "upload-${System.currentTimeMillis()}"
                            val file = File("shared_files/$fileName")
                            part.streamProvider()
                                .use { input -> file.outputStream().use { input.copyTo(it) } }
                        }
                        part.dispose()
                    }
                    call.respondText("File uploaded: $fileName")
                }
            }
        }
        server.start(wait = false)
    }
}

