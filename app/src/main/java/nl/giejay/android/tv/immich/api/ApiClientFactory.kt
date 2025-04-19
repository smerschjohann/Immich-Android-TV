package nl.giejay.android.tv.immich.api

import nl.giejay.android.tv.immich.api.interceptor.ResponseLoggingInterceptor
import nl.giejay.android.tv.immich.api.util.UnsafeOkHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object ApiClientFactory {

    fun configureClientCertificateP12(
        builder: OkHttpClient.Builder,
        p12File: File,
        p12Password: CharArray
    ) {
        if (!p12File.exists()) {
            Timber.i("P12 file does not exist: ${p12File.name}")
            return
        }
        try {
            val keyStore = KeyStore.getInstance("PKCS12")
            FileInputStream(p12File).use { fis ->
                keyStore.load(fis, p12Password)
            }

            // Create a KeyManagerFactory using the loaded KeyStore
            // This factory will provide the client certificate and key during the TLS handshake
            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(keyStore, p12Password)

            // Create TrustManagerFactory (to trust the server)
            // Usually, you still want to trust standard CAs to validate the server certificate.
            // If the server uses a self-signed cert, you'd need a custom TrustManager here.
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(null as KeyStore?) // Initialize with null to use default system CAs
            val trustManagers = tmf.trustManagers
            if (trustManagers.isEmpty() || trustManagers[0] !is X509TrustManager) {
                // Basic check to ensure we got a usable TrustManager
                throw IllegalStateException(
                    "Unexpected default trust managers: ${trustManagers.contentToString()}"
                )
            }
            val trustManager = trustManagers[0] as X509TrustManager

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(kmf.keyManagers, trustManagers, null)
            builder.sslSocketFactory(sslContext.socketFactory, trustManager)

            Timber.i("SSLContext configured successfully using P12 file: ${p12File.name}")
        } catch (e: Exception) {
            Timber.e(e, "Error configuring SSLContext with P12 file [${p12File.name}]: ${e.message}")
        }
    }

    fun getClient(disableSsl: Boolean, apiKey: String, debugMode: Boolean, externalFilesDir: File?): OkHttpClient {
        val apiKeyInterceptor = interceptor(apiKey)
        val builder = if (disableSsl)
            UnsafeOkHttpClient.unsafeOkHttpClient()
        else OkHttpClient.Builder()

        val p12File = File(externalFilesDir ?: File("/storage/emulated/0/Android/data/nl.giejay.android.tv.immich/files"), "immich.p12")
        configureClientCertificateP12(builder, p12File, "immich".toCharArray())

        builder.addInterceptor(apiKeyInterceptor)
        return if (debugMode) {
            builder.addInterceptor(ResponseLoggingInterceptor()).build()
        } else
            builder.build()
    }

    private fun interceptor(apiKey: String): Interceptor = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader("x-api-key", apiKey.trim())
            .build()
        chain.proceed(newRequest)
    }
}