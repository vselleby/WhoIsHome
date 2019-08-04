package com.selleby.whoishome_app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate

class DeviceInformationActivity: AppCompatActivity() {
    private lateinit var connectionState: ConnectionState
    private lateinit var requestQueue: RequestQueue

    private val devicePollingTimer = Timer()
    private var deviceInfoPollingTask: TimerTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionState = intent.getSerializableExtra(MainActivity.CONNECTION_KEY) as ConnectionState
        requestQueue = Volley.newRequestQueue(this)
        launchDeviceInfoTask()
    }

    private fun launchDeviceInfoTask() {
        this.deviceInfoPollingTask = devicePollingTimer.scheduleAtFixedRate(0L, 10000L) {
            val getDeviceInfoUrl = "http://${connectionState.serverAddress}:${connectionState.serverPort}/api/devices"
            val request = JsonObjectRequest(
                Request.Method.GET, getDeviceInfoUrl, null,
                Response.Listener { response ->
                    runOnUiThread(
                        Runnable {
                            Toast.makeText(applicationContext, "Response: $response", Toast.LENGTH_LONG).show()
                        }
                    )
                },
                Response.ErrorListener { error ->
                    runOnUiThread(
                        Runnable {
                            Toast.makeText(applicationContext, "ErrorResponse: $error", Toast.LENGTH_LONG).show()
                            Log.e("TEST", error.toString())
                        }
                    )
                }
            )
            requestQueue.add(request)
        }
    }

    override fun onStop() {
        super.onStop()
        deviceInfoPollingTask?.cancel()
        devicePollingTimer.cancel()
        devicePollingTimer.purge()
    }
}
