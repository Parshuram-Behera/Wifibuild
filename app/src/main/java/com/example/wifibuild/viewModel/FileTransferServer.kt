package com.example.wifibuild.viewModel

import java.io.FileOutputStream
import java.net.ServerSocket

object FileTransferServer {
    fun startFileServer(port: Int, savePath: String) {
        Thread {
            val serverSocket = ServerSocket(port)
            println("File server started on port $port")
            while (true) {
                val socket = serverSocket.accept()
                val inputStream = socket.getInputStream()
                val fileOutput = FileOutputStream(savePath)

                inputStream.copyTo(fileOutput)
                fileOutput.close()
                socket.close()
                println("File received and saved to $savePath")
            }
        }.start()
    }
}