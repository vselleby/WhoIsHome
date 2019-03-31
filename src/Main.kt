import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Timer

fun main() {
    var localAddress = ""
    val datagramSocket = DatagramSocket()
    datagramSocket.use {
        it.connect(InetAddress.getByName("8.8.8.8"), 10002)
        localAddress = datagramSocket.localAddress.hostAddress
    }

    val networkPoller = NetworkPoller(localAddress)
    networkPoller.initialise()
    Timer().scheduleAtFixedRate(networkPoller, 1000, 100000)
}