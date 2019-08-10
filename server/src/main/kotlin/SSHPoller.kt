import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import device.Device
import device.DeviceHandler
import device.FrequencyBand
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.TimerTask

class SSHPoller(private val user: String, private val keyPath: String, private val ipAddress: String, private val deviceHandler: DeviceHandler) : TimerTask() {
    private val connectedDevicesPath = "/tmp/clientlist.json"
    private val jsch = JSch()
    private lateinit var session : Session
    private lateinit var channel : ChannelExec

    fun initialise() {
        jsch.addIdentity(keyPath)
        session = jsch.getSession(user, ipAddress, 449)
        session.setConfig("StrictHostKeyChecking", "no")

        session.connect()
    }

    override fun run() {
        try {
            channel = session.openChannel("exec") as ChannelExec
            channel.setCommand("cat $connectedDevicesPath")
            channel.connect()
            val bufferedReader = BufferedReader(InputStreamReader(channel.inputStream))
            val deviceJsonString = bufferedReader.lines().reduce { s1: String?, s2: String? ->  s1.plus(s2) }.orElse("")
            if (deviceJsonString.isNotEmpty()) {
                parseJsonString(deviceJsonString)

            }
        }
        finally {
            channel.disconnect()
        }
    }

    private fun parseJsonString(jsonString: String) {
        val foundDevices = HashSet<Device>()
        val rootNode = ObjectMapper().readTree(jsonString)
        val fiveGNodes: JsonNode? = rootNode.findValue("5G")
        val twoGNodes: JsonNode? = rootNode.findValue("2G")

        fiveGNodes?.fields()?.forEach {
            foundDevices.add(parseDevice(it, FrequencyBand.FIVE_GHZ))
        }

        twoGNodes?.fields()?.forEach {
            foundDevices.add(parseDevice(it, FrequencyBand.TWO_GHZ))
        }
        deviceHandler.updateConnectedDevices(foundDevices)
    }

    private fun parseDevice(jsonField: Map.Entry<String, JsonNode>, frequencyBand: FrequencyBand): Device {
        val macAddress = jsonField.key
        val ipAddress = jsonField.value.findValue("ip").asText()
        val rssi = jsonField.value.findValue("rssi").asInt()
        return Device(ipAddress, macAddress, rssi, frequencyBand)
    }

}
