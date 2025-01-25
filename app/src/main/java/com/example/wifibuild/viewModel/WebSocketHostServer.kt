package com.example.wifibuild.viewModel

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class WebSocketHostServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("Client connected: ${conn.remoteSocketAddress}")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("Client disconnected: ${conn.remoteSocketAddress}")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        println("Message from client: $message")
        conn.send("Echo from server: $message")
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        println("Error: ${ex.message}")
    }

    override fun onStart() {
        println("WebSocket server started on port $port")
    }
}
