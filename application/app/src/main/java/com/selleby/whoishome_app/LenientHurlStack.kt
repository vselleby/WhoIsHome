package com.selleby.whoishome_app

import com.android.volley.toolbox.HurlStack
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class LenientHurlStack(private val socketFactory: SSLSocketFactory) : HurlStack(null, socketFactory) {
    override fun createConnection(url: URL?): HttpURLConnection {
        val httpsURLConnection: HttpsURLConnection = super.createConnection(url) as HttpsURLConnection
        httpsURLConnection.sslSocketFactory = socketFactory
        httpsURLConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
        return httpsURLConnection
    }
}