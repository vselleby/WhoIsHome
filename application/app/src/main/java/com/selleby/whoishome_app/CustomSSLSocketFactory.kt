package com.selleby.whoishome_app

import android.content.Context
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory


object CustomSSLSocketFactory {
    private val KEYSTORE_PASSWORD = "3lefantbajs_client".toCharArray()
    private val TRUSTSTORE_PASSWORD = "3lefantbajs_trust".toCharArray()
    @Volatile private var FACTORY : SSLSocketFactory? = null


    fun get(context : Context) : SSLSocketFactory =
        FACTORY ?: synchronized(this) {
            FACTORY ?: createFactory(context).also { FACTORY = it }
    }

    private fun createFactory(context : Context) : SSLSocketFactory {
        try {
            val trustStore = KeyStore.getInstance("BKS")
            val keyStore = KeyStore.getInstance("BKS")
            context.resources.openRawResource(R.raw.clienttruststore).use {
                trustStore.load(it, TRUSTSTORE_PASSWORD)
            }
            context.resources.openRawResource(R.raw.client).use {
                keyStore.load(it, KEYSTORE_PASSWORD)
            }


            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(trustStore)
            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
            return sslContext.socketFactory
        } catch (e: Exception) {
            throw AssertionError(e)
        }

    }
}