class Device(val ipAddress: String, val macAddress: String, var rssi: Int, var frequencyBand: FrequencyBand, var name: String) {
    constructor(ipAddress: String, macAddress: String) : this(ipAddress, macAddress, 0, FrequencyBand.UNKNOWN, "")
    constructor(ipAddress: String, macAddress: String, rssi: Int, frequencyBand: FrequencyBand) : this(ipAddress, macAddress, rssi, frequencyBand, "")

    override fun toString(): String {
        return "Device(ipAddress='$ipAddress', macAddress='$macAddress', rssi='$rssi', frequencyBand='$frequencyBand', name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Device

        if (ipAddress != other.ipAddress) return false
        if (macAddress != other.macAddress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ipAddress.hashCode()
        result = 31 * result + macAddress.hashCode()
        return result
    }
}