package com.selleby.whoishome_app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
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
            val getDeviceInfoUrl = "https://${connectionState.serverAddress}:${connectionState.serverPort}/api/devices"
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

    private fun sendDeviceModificationRequest(device: DeviceInformation, name: String?, notificationEnabled: Boolean?) {
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("notificationEnabled", notificationEnabled)
        Log.i(TAG, "Sending deviceModificationRequest with name:$name notificationEnabled:$notificationEnabled")
        val getDeviceInfoUrl = "https://${connectionState.serverAddress}:${connectionState.serverPort}/api/devices/${device.macAddress.replace(":", 	"%3A")}"
        val request = object : JsonObjectRequest(
            Method.POST,
            getDeviceInfoUrl,
            jsonObject,
            Response.Listener { response ->
                val modifiedDevice = parseDeviceInformation(response)
                devices.remove(modifiedDevice)
                devices.add(modifiedDevice)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error when modifying device: $error")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }

        requestQueue.add(request)

    }

    private fun parseDeviceInformationArray(jsonArray: JSONArray) {
        val currentDevices = HashSet<DeviceInformation>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            currentDevices.add(parseDeviceInformation(jsonObject))
        }

        devices.removeAll(devices.minus(currentDevices))
        devices.addAll(currentDevices)
        updateTable()
    }

    private fun parseDeviceInformation(jsonObject: JSONObject): DeviceInformation {
        val ipAddress: String = jsonObject.get("ipAddress") as String
        val macAddress: String = jsonObject.get("macAddress") as String
        val name = jsonObject.get("name") as String
        return DeviceInformation(ipAddress, macAddress, name, false)
    }

    private fun updateTable() {
        for (device : DeviceInformation in devices) {
            val tableRow = updateTableRow(device)
            if (tableRow.parent == null) {
                tableLayout.addView(tableRow)
            }
        }
    }

    private fun updateTableRow(device: DeviceInformation): TableRow {
        val tableRow = device.tableRow ?: layoutInflater.inflate(R.layout.table_row_device_template, null) as TableRow

        val nameEditText = tableRow.findViewById<EditText>(R.id.device_name_value)
        if (!nameEditText.hasFocus()) {
            nameEditText.setText(device.name)
        }
        tableRow.findViewById<TextView>(R.id.ip_address_value).text = device.ipAddress
        tableRow.findViewById<TextView>(R.id.mac_address_value).text = device.macAddress
        tableRow.findViewById<CheckBox>(R.id.notification_value).isChecked = device.notification

        if (device.tableRow == null) {
            addDeviceListeners(tableRow, device)
            device.tableRow = tableRow
        }
        return tableRow
    }

    private fun addDeviceListeners(tableRow: TableRow, device: DeviceInformation) {
        val nameEditText = tableRow.findViewById<EditText>(R.id.device_name_value)
        val notificationCheckBox = tableRow.findViewById<CheckBox>(R.id.notification_value)

        notificationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != device.notification) {
                sendDeviceModificationRequest(device, null, isChecked)
            }
        }
        
        nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && nameEditText.text.toString() != device.name) {
                sendDeviceModificationRequest(device, nameEditText.text.toString(), null)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        deviceInfoPollingTask?.cancel()
        devicePollingTimer.cancel()
        devicePollingTimer.purge()
    }

    companion object {
        private val TAG = DeviceInformationActivity::class.java.name
    }

    data class DeviceInformation(var ipAddress: String, var macAddress: String, var name: String, var notification: Boolean) {
        var tableRow: TableRow? = null

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
