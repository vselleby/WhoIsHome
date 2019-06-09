import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.TimerTask

class SSHPoller(private val user: String, private val keyPath: String, private val ipAddress: String) : TimerTask() {
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
        println(jsonString)

    }
}
