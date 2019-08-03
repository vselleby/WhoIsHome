package com.selleby.whoishome_app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity(), ConnectionListener {
    private val connectionState = ConnectionState()
    private lateinit var serverConnectionButton: Button
    private lateinit var deviceInformationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectionState.addConnectedListener(this)
        createButtons()
    }

    private fun createButtons() {
        serverConnectionButton = findViewById<Button>(R.id.server_button)
        deviceInformationButton = findViewById<Button>(R.id.device_information_button)

        serverConnectionButton.setOnClickListener {
            val serverConnectionDialog = ServerConnectionDialog(this, connectionState)
            serverConnectionDialog.show()
        }
    }

    override fun connectionStateUpdated(connected: Boolean) {
        deviceInformationButton.isEnabled = connected
    }
}
