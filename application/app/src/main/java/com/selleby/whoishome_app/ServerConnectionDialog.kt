package com.selleby.whoishome_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.Timer
import kotlin.concurrent.schedule

class ServerConnectionDialog(context: Context, private val connectionStateHandler: ConnectionStateHandler) : ConnectionDialog(context) {
    private var serverAddressEditText : EditText? = null
    private var serverPortEditText : EditText? = null

    override fun createConnectButtonListener(): View.OnClickListener {
        return View.OnClickListener {
            connectionStateHandler.connected = false
            if (serverAddressEditText?.text.isNullOrEmpty() || serverPortEditText?.text.isNullOrEmpty()) {
                displayConnectionError("Server IP-address and port has to be provided")
                return@OnClickListener
            }
            val serverAddress = serverAddressEditText?.text!!.toString()
            val port = serverPortEditText?.text!!.toString().let { Integer.parseInt(it) }
            if (port == 0) {
                displayConnectionError("Port cannot be 0")
                return@OnClickListener
            }
            connectionStateHandler.connectionState.serverAddress = serverAddress
            connectionStateHandler.connectionState.serverPort = port
            Timer().schedule(0) {
                try {
                    val inetAddress = InetAddress.getByName(serverAddress)
                    val requestQueue = Volley.newRequestQueue(context)
                    requestQueue.add(
                        JsonObjectRequest(
                            Request.Method.GET,
                            "http://${inetAddress.hostAddress}:$port/api/devices/ping",
                            null,
                            Response.Listener { response ->
                                Toast.makeText(context, "Successfully connected to server", Toast.LENGTH_LONG).show()
                                connectionStateHandler.connected = true
                                dismiss()
                                requestQueue.stop()
                            },
                            Response.ErrorListener { error ->
                                displayConnectionError("Error when trying to connect to server:  ${error?.networkResponse?.statusCode}")
                                requestQueue.stop()
                            }
                        )
                    )
                } catch (e: UnknownHostException) {
                    displayConnectionError("Invalid Server Address: $serverAddress")
                }
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