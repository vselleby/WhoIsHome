import webapi.WebServer
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Timer

fun main() {
    val deviceHandler =  DeviceHandler()
    Context.instance.deviceHandler = deviceHandler

    var localAddress = ""
    val datagramSocket = DatagramSocket()
    datagramSocket.use {
        it.connect(InetAddress.getByName("8.8.8.8"), 10002)
        localAddress = datagramSocket.localAddress.hostAddress
    }

    val networkPoller = NetworkPoller(localAddress, deviceHandler)
    networkPoller.initialise()
    Timer().scheduleAtFixedRate(networkPoller, 1000, 100000)


    val webServer = WebServer()
    webServer.start()
}