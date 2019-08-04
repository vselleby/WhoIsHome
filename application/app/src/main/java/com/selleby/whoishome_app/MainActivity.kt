package com.selleby.whoishome_app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity(), ConnectionListener {
    private val connectionStateHandler = ConnectionStateHandler()
    private lateinit var serverConnectionButton: Button
    private lateinit var deviceInformationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectionStateHandler.addConnectedListener(this)
        createButtons()
    }

    private fun createButtons() {
        serverConnectionButton = findViewById(R.id.server_button)
        deviceInformationButton = findViewById(R.id.device_information_button)

        serverConnectionButton.setOnClickListener {
            val serverConnectionDialog = ServerConnectionDialog(this, connectionStateHandler)
            serverConnectionDialog.show()
        }

        deviceInformationButton.setOnClickListener {
            val intent = Intent(this, DeviceInformationActivity::class.java)
            val connectionStateBundle = Bundle()
            connectionStateBundle.putSerializable(CONNECTION_KEY, connectionStateHandler.connectionState)
            intent.putExtras(connectionStateBundle)
            startActivity(intent)
        }

    }

    override fun connectionStateUpdated(connected: Boolean) {
        deviceInformationButton.isEnabled = connected
    }

    companion object {
        const val CONNECTION_KEY = "connection_key"
    }
}
