package com.selleby.whoishome_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import java.net.InetAddress
import java.net.UnknownHostException

class ServerConnectionDialog(context: Context, private val connectionStateHandler: ConnectionStateHandler) : ConnectionDialog(context) {
    private var serverAddressEditText : EditText? = null
    private var serverPortEditText : EditText? = null

    override fun createConnectButtonListener(): View.OnClickListener {
        return View.OnClickListener {
            if (serverAddressEditText?.text.isNullOrEmpty() || serverPortEditText?.text.isNullOrEmpty()) {
                displayConnectionError("Server IP-address and port has to be provided")
                connectionStateHandler.connected = false
                return@OnClickListener
            }
            val serverAddress = serverAddressEditText?.text!!.toString()
            val port = serverPortEditText?.text!!.toString().let { Integer.parseInt(it) }
            connectionStateHandler.connectionState.serverAddress = serverAddress
            connectionStateHandler.connectionState.serverPort = port
            var errorMessage = ""
            val networkThread =  Thread {
                try {
                    val inetAddress = InetAddress.getByName(serverAddress)
                    if (!inetAddress.isReachable(1000)) {
                        errorMessage = "Provided address: ${inetAddress.hostName} not reachable"
                    }

                } catch (e: UnknownHostException) {
                    errorMessage = "Invalid Server Address: $serverAddress"
                }
            }
            networkThread.start()
            networkThread.join()
            if (errorMessage.isNotEmpty()) {
                displayConnectionError(errorMessage)
            }
            else {
                connectionStateHandler.connected = true
                dismiss()
            }
        }
    }


    override fun createSpecificView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_server, null)
        serverAddressEditText = view.findViewById(R.id.server_address_edit_text)
        serverPortEditText = view.findViewById(R.id.server_port_edit_text)
        return view
    }

}