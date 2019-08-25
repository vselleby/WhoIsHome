package device

import java.util.logging.Logger
import javax.ws.rs.NotFoundException

class DeviceHandler(private val devicePersistor: DevicePersistor) {
    val connectedDevices = HashSet<Device>()
    var persistentDevices = HashSet<Device>()
    private val logger = Logger.getLogger(DeviceHandler::class.java.name)

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
            logger.info("New device discovered: $it")
        }
        removedDevices.forEach {
            logger.info("Device no longer connected: $it")
        }
    }

    private fun updateExistingDevices(discoveredDevices: HashSet<Device>) {
        discoveredDevices.forEach { discoveredDevice ->
            connectedDevices.forEach { connectedDevice ->
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
                    NotFoundException("Device with MacAddress: $macAddress not found")
                }
    }

    fun setNotification(macAddress: String, notificationEnabled: Boolean) {
        val device = getDevice(macAddress)
        device.notificationEnabled = notificationEnabled
        persistentDevices.remove(device)
        persistentDevices.add(device)
        devicePersistor.save(persistentDevices.toList())
    }

    fun setName(macAddress: String, name: String) {
        val device = getDevice(macAddress)
        device.name = name
        persistentDevices.remove(device)
        persistentDevices.add(device)
        devicePersistor.save(persistentDevices.toList())
    }
}