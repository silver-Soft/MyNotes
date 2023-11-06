package com.fcbiyt.mynotes.core

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitHelper {//Se usa un objeto por que se quiere acceder al contenido, alternativo a un companionObject
    fun getRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .sslSocketFactory(getUnsafeSslSocketFactory(), getTrustAllCertificates())//<----- Deshabilita la verificacion de autofirmas de certificados
            .hostnameVerifier(getTrustAllHostnames())//<---- Admitir todos los HostNames
            .build()

        return Retrofit.Builder()
            .baseUrl("https://172.16.12.65:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }


    /**
     * Las siguientes funciones admiten la recepcion y transferencia de datos a cualquier nombre de host
     * incluidos los autofirmados
     * NO ES RECOMENDABLE EN PRODUCCION esto se hace de forma provicional
     */
    private fun getUnsafeSslSocketFactory(): SSLSocketFactory {
        try {
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            return sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getTrustAllCertificates(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun getTrustAllHostnames(): HostnameVerifier {
        return HostnameVerifier { hostname, session -> true }
    }
}