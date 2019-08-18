package device

import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("ipAddress", "macAddress", "rssi", "frequencyBand", "name", "notificationEnabled")
class Device(var ipAddress: String, val macAddress: String, var rssi: Int, var frequencyBand: FrequencyBand, var name: String, var notificationEnabled: Boolean) {
    constructor(ipAddress: String, macAddress: String) : this(ipAddress, macAddress, 0, FrequencyBand.UNKNOWN, "", false)
    constructor(ipAddress: String, macAddress: String, rssi: Int, frequencyBand: FrequencyBand) : this(ipAddress, macAddress, rssi, frequencyBand, "", false)

    override fun toString(): String {
        return "device.Device(ipAddress='$ipAddress', macAddress='$macAddress', rssi='$rssi', frequencyBand='$frequencyBand', name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        if (macAddress != other.macAddress) return false

        return true
    }

    override fun hashCode(): Int {
        return macAddress.hashCode()
    }


}