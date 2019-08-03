package com.selleby.whoishome_app

import java.io.Serializable

class ConnectionState : Serializable {
    private val connectionListeners = ArrayList<ConnectionListener>()
    var connected = false
        set(value) {
            field = value
            notifyListeners()
        }
    var serverAddress = ""
    var serverPort = 0
    var usingVpn = false
    var vpnAddress = ""
    var vpnPort = 0

    fun addConnectedListener(listener: ConnectionListener) {
        connectionListeners.add(listener)
    }

    private fun notifyListeners() {
        connectionListeners.forEach { it.connectionStateUpdated(connected) }
    }
}