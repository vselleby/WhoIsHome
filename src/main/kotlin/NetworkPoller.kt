
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.util.TimerTask
import java.util.stream.Collectors

class NetworkPoller(var localAddress: String, val deviceHandler: DeviceHandler) : TimerTask() {
    private var baseAddress : String = ""

    fun initialise() {
        val splittedAddress = localAddress.split(".")
        baseAddress = splittedAddress[0].plus(".".plus(splittedAddress[1].plus(".").plus(splittedAddress[2].plus("."))))
    }
    override fun run() {
        val discoveredDevices = ArrayList<Device>()
        val reachableAddresses = HashSet<InetAddress>()
        for (i in 0..254) {
            val addressToTest = InetAddress.getByName(baseAddress.plus(i))
            println("Trying: " + baseAddress.plus(i))
            if (addressToTest.isReachable(100)) {
                println("Reached: " + baseAddress.plus(i))
                reachableAddresses.add(addressToTest)
            }
        }

        val buf = BufferedReader(FileReader("/proc/net/arp"))
        buf.lines().forEach { arpLine ->
            reachableAddresses.forEach { reachableAddress ->
                if (reachableAddress.hostName in arpLine) {
                    discoveredDevices.add(parseDeviceInformationFromArpString(arpLine))
                }
            }
        }
        updateConnectedDevices(discoveredDevices)
    }

    private fun updateConnectedDevices(discoveredDevices: ArrayList<Device>) {
        val newDevices = discoveredDevices.toSet().minus(deviceHandler.connectedDevices.toSet())
        val removedDevices = deviceHandler.connectedDevices.toSet().minus(discoveredDevices.toSet())

        deviceHandler.connectedDevices.addAll(newDevices)
        deviceHandler.connectedDevices.removeAll(removedDevices)

        println("New devices discovered: \n")
        newDevices.forEach(System.out::println)

        println("Devices no longer connected: \n")
        removedDevices.forEach(System.out::println)
    }

    private fun parseDeviceInformationFromArpString(arpLine: String): Device {
        val ipAndMac = arpLine.split(" ").
                stream().
                filter { entry ->
                    entry.length > 10
                }.
                collect(Collectors.toList())
        assert(ipAndMac.size == 2)
        return Device(ipAndMac[0], ipAndMac[1])
    }
}

