// src/main/java/com/example/chatapp/api/RetrofitClient.kt
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://gmbh-lightning-database-dx.trycloudflare.com/" // Replace with your server IP

    fun create(): AuthService {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)  // Connection timeout
            .readTimeout(15, TimeUnit.SECONDS)     // Read timeout
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}