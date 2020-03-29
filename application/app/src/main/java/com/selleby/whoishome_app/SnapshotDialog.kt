package com.selleby.whoishome_app

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView

class SnapshotDialog(context: Context, private val bitmap: Bitmap): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_snapshot)
        val snapshotImage = findViewById<ImageView>(R.id.snapshot_image)
        snapshotImage.setImageBitmap(bitmap)

        val closeButton = findViewById<Button>(R.id.snapshot_close_button)
        closeButton.setOnClickListener { dismiss() }
    }
}