package com.nahid.expensetracker.data.di

import com.nahid.expensetracker.core.utils.NetworkHelper
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val TAG = "NetworkModule"

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    single { NetworkHelper(androidContext()) }
/*    single<TokenManager> { TokenManagerImpl(get()) }
    single {
        HttpClient {
            val tokenManager = get<TokenManager>()
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = ServerConstants.REQUEST_TIMEOUT * 1000
                connectTimeoutMillis = ServerConstants.CONNECTION_TIMEOUT * 1000
                socketTimeoutMillis = ServerConstants.SOCKET_TIMEOUT * 1000
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(TAG, "NETWORK_LOG : $message")
                    }
                }
                level = LogLevel.BODY
            }
            install(DefaultRequest) {
                url.takeFrom(ServerConstants.BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)

                val token = runBlocking { tokenManager.getAccessToken() }
                if (token.isNotEmpty()) {
                    header(HttpHeaders.Cookie, token)
                }

            }
        }
    }*/
}