package com.selleby.whoishome_app

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout

abstract class ConnectionDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_connection)

        val layout = findViewById<RelativeLayout>(R.id.connection_layout)
        layout.addView(createSpecificView())

        addButtonListeners()
    }

    private fun addButtonListeners() {
        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dismiss()
        }
        findViewById<Button>(R.id.connect_button).setOnClickListener(createConnectButtonListener())
    }

    protected fun displayConnectionError(message: String) {
        val builder = AlertDialog.Builder(context)
        with(builder) {
            setTitle("Connection error")
            setMessage(message)
            setPositiveButton("OK", null)
            show()
        }
    }

    abstract fun createSpecificView() : View

    abstract fun createConnectButtonListener() : View.OnClickListener
}