package com.selleby.whoishome_app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate

class DeviceInformationActivity: AppCompatActivity() {
    private lateinit var connectionState: ConnectionState
    private lateinit var requestQueue: RequestQueue
    private lateinit var tableLayout: TableLayout
    private lateinit var informationTableRow: TableRow

    private val devices = HashSet<DeviceInformation>()
    private val devicePollingTimer = Timer()
    private var deviceInfoPollingTask: TimerTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_information)
        tableLayout = findViewById(R.id.table_layout)
        informationTableRow = findViewById(R.id.information_table_row)
        connectionState = intent.getSerializableExtra(MainActivity.CONNECTION_KEY) as ConnectionState
        requestQueue = Volley.newRequestQueue(this)
        launchDeviceInfoTask()
    }

    private fun launchDeviceInfoTask() {
        this.deviceInfoPollingTask = devicePollingTimer.scheduleAtFixedRate(0L, 10000L) {
            val getDeviceInfoUrl = "http://${connectionState.serverAddress}:${connectionState.serverPort}/api/devices"
            val request = JsonArrayRequest(
                Request.Method.GET, getDeviceInfoUrl, null,
                Response.Listener { response ->
                    parseDeviceInformationArray(response)

                },
                Response.ErrorListener { error ->
                    Log.e(TAG, "Error when fetching device information: $error")
                }
            )
            requestQueue.add(request)
        }
    }

    private fun parseDeviceInformationArray(jsonArray: JSONArray) {
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            parseDeviceInformation(jsonObject)
            updateTable()
        }
    }

    private fun updateTable() {
        tableLayout.removeAllViews()
        tableLayout.addView(informationTableRow)
        for (device : DeviceInformation in devices) {
            tableLayout.addView(createTableRow(device))
        }
    }

    private fun createTableRow(device: DeviceInformation): TableRow {
        val tableRow = TableRow(this)
        val nameView = TextView(this)
        nameView.text = device.name
        val ipAddressView = TextView(this)
        ipAddressView.text = device.ipAddress
        val macAddressView = TextView(this)
        macAddressView.text = device.macAddress
        val notificationCheckBox = CheckBox(this)
        notificationCheckBox.isChecked = device.notification
        tableRow.addView(nameView)
        tableRow.addView(ipAddressView)
        tableRow.addView(macAddressView)
        tableRow.addView(notificationCheckBox)
        return tableRow
    }

    private fun parseDeviceInformation(jsonObject: JSONObject) {
        val ipAddress: String = jsonObject.get("ipAddress") as String
        val macAddress: String = jsonObject.get("macAddress") as String
        val name = jsonObject.get("name") as String
        val device = DeviceInformation(ipAddress, macAddress, name, false)
        devices.remove(device)
        devices.add(device)
    }

    override fun onStop() {
        super.onStop()
        deviceInfoPollingTask?.cancel()
        devicePollingTimer.cancel()
        devicePollingTimer.purge()
    }

    companion object {
        private val TAG = DeviceInformationActivity::class.java.name;
    }

    data class DeviceInformation(var ipAddress: String, var macAddress: String, var name: String, var notification: Boolean) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DeviceInformation

            if (macAddress != other.macAddress) return false

            return true
        }

        override fun hashCode(): Int {
            return macAddress.hashCode()
        }
    }
}
