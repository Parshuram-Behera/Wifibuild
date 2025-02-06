package com.example.wifibuild.viewModel

import java.io.File
import java.io.FileInputStream
import java.net.Socket

object FileTransferClient {
    fun sendFileToHost(hostAddress: String, port: Int, filePath: String) {
        Thread {
            val socket = Socket(hostAddress, port)
            val fileInput = FileInputStream(File(filePath))
            val outputStream = socket.getOutputStream()

            fileInput.copyTo(outputStream)
            fileInput.close()
            outputStream.close()
            socket.close()
            println("File sent to host at $hostAddress")
        }.start()
    }
}