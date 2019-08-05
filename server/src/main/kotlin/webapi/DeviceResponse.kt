package webapi

import Device
import com.fasterxml.jackson.annotation.JsonPropertyOrder


@SuppressWarnings("WeakerAccess")
@JsonPropertyOrder("ipAddress", "macAddress", "rssi", "frequencyBand", "name")
class DeviceResponse(device: Device) {
    val ipAddress = device.ipAddress
    val macAddress = device.macAddress
    val rssi = device.rssi
    val frequencyBand = device.frequencyBand
    val name = device.name
}