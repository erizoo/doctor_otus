package ru.soft

import android.content.*
import com.google.gson.*
import dagger.*
import okhttp3.*
import okhttp3.logging.*
import retrofit2.*
import retrofit2.converter.gson.*
import retrofit2.converter.scalars.*
import ru.soft.core_impl.*
import java.security.*
import java.security.cert.*
import java.util.concurrent.*
import javax.inject.*
import javax.net.ssl.*


@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header(
                        "Authorization",
                        Credentials.basic(BuildConfig.USER, BuildConfig.PASSWORD)
                    )
                val request = requestBuilder.build()
                chain.proceed(request)
            }
        if (!BuildConfig.DEBUG) {
            val (sslContext, trustManager) = getSSLConfig(context)
                ?: throw IllegalStateException("Failed to get SSL config")
            builder.sslSocketFactory(sslContext.socketFactory, trustManager)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    private fun getSSLConfig(context: Context): Pair<SSLContext, X509TrustManager>? {
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val inputStream =
                context.resources.openRawResource(R.raw.cer) // TODO: Поменяйте на ваш ресурс
            val certificate = certificateFactory.generateCertificate(inputStream)
            inputStream.close()

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("cer", certificate)

            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)

            val trustManagers = trustManagerFactory.trustManagers
            check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) { "Unexpected default trust managers:" + trustManagers.contentToString() }
            val trustManager = trustManagers[0] as X509TrustManager

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(trustManager), null)

            return Pair(sslContext, trustManager)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}