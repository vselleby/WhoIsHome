import webapi.WebServer
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Timer

fun main(args: Array<String>) {
    val deviceHandler =  DeviceHandler()
    Context.instance.deviceHandler = deviceHandler

    when {
        args.size == 3 -> startSSHPoller(args[0], args[1], args[2])
        args.isEmpty() -> startNetworkPoller(deviceHandler)
        else -> {
            println("Usage: Requires <user>,  <Path to ssh key> and <IP address to router> for ASUS routers and no arguments for Ping polling")
            return
        }
    }
    val webServer = WebServer()
    webServer.start()
    Runtime.getRuntime().addShutdownHook(Thread { webServer.stop() })
}

fun startNetworkPoller(deviceHandler: DeviceHandler) {
    var localAddress = ""
    val datagramSocket = DatagramSocket()
    datagramSocket.use {
        it.connect(InetAddress.getByName("8.8.8.8"), 10002)
        localAddress = datagramSocket.localAddress.hostAddress
    }

    val networkPoller = NetworkPoller(localAddress, deviceHandler)
    networkPoller.initialise()
    Timer().scheduleAtFixedRate(networkPoller, 1000, 20000)
}

fun startSSHPoller(user: String, keyPath: String, ipAddress: String) {
    val sshPoller = SSHPoller(user, keyPath, ipAddress)
    sshPoller.initialise()
    Timer().scheduleAtFixedRate(sshPoller, 1000, 10000)

}