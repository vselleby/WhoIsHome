package com.selleby.whoishome_app

import java.io.Serializable

data class ConnectionState(
    var connected: Boolean = false, var serverAddress: String = "", var serverPort: Int = 0,
    var usingVpn: Boolean = false, var vpnAddress: String = "", var vpnPort: Int = 0
) : Serializable