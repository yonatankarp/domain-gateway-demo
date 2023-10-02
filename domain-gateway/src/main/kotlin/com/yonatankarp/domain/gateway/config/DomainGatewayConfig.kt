package com.yonatankarp.domain.gateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.hello.openapi.v1_current.HelloApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.openapitools.client.infrastructure.ApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Converter.Factory
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class DomainGatewayConfig {
    private fun okHttpClientBuilder() =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = BODY },
            )

    @Bean
    fun jacksonConverterFactory(objectMapper: ObjectMapper): JacksonConverterFactory = JacksonConverterFactory.create(objectMapper)

    @Bean
    fun helloApiClient(
        objectMapper: ObjectMapper,
        converterFactories: List<Factory>,
    ): HelloApi =
        ApiClient(
            baseUrl = "http://hello-service:8080",
            serializerBuilder = objectMapper,
            okHttpClientBuilder = okHttpClientBuilder(),
            converterFactories = converterFactories,
        ).createService(HelloApi::class.java)

    @Bean
    fun goodbyeApiClient(
        objectMapper: ObjectMapper,
        converterFactories: List<Factory>,
    ): GoodbyeApi =
        ApiClient(
            baseUrl = "http://goodbyte-service:8789",
            serializerBuilder = objectMapper,
            okHttpClientBuilder = okHttpClientBuilder(),
            converterFactories = converterFactories,
        ).createService(GoodbyeApi::class.java)
}
