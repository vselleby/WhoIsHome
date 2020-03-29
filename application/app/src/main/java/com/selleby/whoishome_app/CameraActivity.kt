package com.selleby.whoishome_app

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley

class CameraActivity: AppCompatActivity() {
    private lateinit var connectionState: ConnectionState
    private lateinit var requestQueue: RequestQueue
    private lateinit var snapshotButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        requestQueue = Volley.newRequestQueue(this, LenientHurlStack(CustomSSLSocketFactory.get(applicationContext)))
        connectionState = intent.getSerializableExtra(MainActivity.CONNECTION_KEY) as ConnectionState

        snapshotButton = findViewById(R.id.snapshot_button)
        snapshotButton.setOnClickListener { requestSnapshot() }
    }

    private fun requestSnapshot() {
        val getImageURL = "https://${connectionState.serverAddress}:${connectionState.serverPort}/api/camera/image"
        val request = ImageRequest(
            getImageURL,
            Response.Listener {
                runOnUiThread {
                    SnapshotDialog(this, it).show()
                }
            },
            0,
            0,
            ImageView.ScaleType.CENTER,
            Bitmap.Config.RGB_565,
            Response.ErrorListener {
                Log.e(TAG, "Error when modifying device: $it")

            }
        )

        requestQueue.add(request)
    }

    companion object {
        private val TAG = CameraActivity::class.java.name
    }
}
