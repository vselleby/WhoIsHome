package com.selleby.whoishome_app

class ConnectionStateHandler {
    private val connectionListeners = ArrayList<ConnectionListener>()
    val connectionState = ConnectionState()
    var connected = false
        set(value) {
            field = value
            connectionState.connected = value
            notifyListeners()
        }

    fun addConnectedListener(listener: ConnectionListener) {
        connectionListeners.add(listener)
    }

    private fun notifyListeners() {
        connectionListeners.forEach { it.connectionStateUpdated(connected) }
    }
}