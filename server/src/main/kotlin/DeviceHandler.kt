class DeviceHandler {
    val connectedDevices = HashSet<Device>()
    val mobileDevices = HashSet<Device>()

    fun updateConnectedDevices(discoveredDevices: HashSet<Device>) {
        val newDevices = discoveredDevices.minus(connectedDevices)
        val removedDevices = connectedDevices.minus(discoveredDevices)

        connectedDevices.addAll(newDevices)
        connectedDevices.removeAll(removedDevices)

        println("New devices discovered: \n")
        newDevices.forEach(System.out::println)

        println("Devices no longer connected: \n")
        removedDevices.forEach(System.out::println)
    }
}