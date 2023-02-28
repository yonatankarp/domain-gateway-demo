package com.yonatankarp.domain.gateway.demo.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.hello.openapi.v1_current.HelloApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class DomainGatewayConfig {

    private fun okHttpClient() =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = BODY },
            )
            .build()

    @Bean
    fun jacksonConverterFactory(objectMapper: ObjectMapper): JacksonConverterFactory =
        JacksonConverterFactory.create(objectMapper)

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun helloApiClient(converterFactory: Factory): HelloApi =
        Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .baseUrl("http://hello-service:8080")
            .client(okHttpClient())
            .build()
            .create(HelloApi::class.java)

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun goodbyeApiClient(converterFactory: Factory): GoodbyeApi =
        Retrofit
            .Builder()
            .addConverterFactory(converterFactory)
            .baseUrl("http://goodbyte-service:8789")
            .client(okHttpClient())
            .build()
            .create(GoodbyeApi::class.java)
}
