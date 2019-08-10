package device

import javax.ws.rs.NotFoundException

class DeviceHandler(private val devicePersistor: DevicePersistor) {
    val connectedDevices = HashSet<Device>()
    var persistentDevices = HashSet<Device>()

    fun updateConnectedDevices(discoveredDevices: HashSet<Device>) {
        updateExistingDevices(discoveredDevices)

        val newDevices = discoveredDevices.minus(connectedDevices)
        val removedDevices = connectedDevices.minus(discoveredDevices)

        newDevices.forEach {
            if (persistentDevices.contains(it)) {
                setPersistentValues(it)
            }
        }

        connectedDevices.removeAll(removedDevices)
        connectedDevices.addAll(newDevices)

        newDevices.forEach {
            println("New device discovered: $it")
        }
        removedDevices.forEach {
            println("device.Device no longer connected: $it")
        }
    }

    private fun updateExistingDevices(discoveredDevices: HashSet<Device>) {
        discoveredDevices.forEach { discoveredDevice ->
            connectedDevices.stream().map { connectedDevice ->
                if (discoveredDevice == connectedDevice) {
                    connectedDevice.ipAddress = discoveredDevice.ipAddress
                    connectedDevice.frequencyBand = discoveredDevice.frequencyBand
                    connectedDevice.rssi = discoveredDevice.rssi
                }
            }
        }
    }

    private fun setPersistentValues(device: Device) {
        val persistentDevice = persistentDevices.find { persistentDevice ->
            persistentDevice == device
        }
        if (persistentDevice != null) {
            device.name = persistentDevice.name
            device.notificationEnabled = persistentDevice.notificationEnabled
        }
    }

    fun getDevice(macAddress: String) : Device {
        return connectedDevices.stream().
                filter {
                    it.macAddress == macAddress
                }.
                findFirst().
                orElseThrow {
                    NotFoundException()
                }
    }

    fun setNotification(macAddress: String, notificationEnabled: Boolean) {
        val device = getDevice(macAddress)
        device.notificationEnabled = notificationEnabled
        persistentDevices.remove(device)
        persistentDevices.add(device)
        devicePersistor.save(connectedDevices.toList())
    }

    fun setName(macAddress: String, name: String) {
        val device = getDevice(macAddress)
        device.name = name
        persistentDevices.remove(device)
        persistentDevices.add(device)
        devicePersistor.save(connectedDevices.toList())
    }
}