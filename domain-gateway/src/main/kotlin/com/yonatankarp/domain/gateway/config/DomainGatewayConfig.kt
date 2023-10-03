package com.yonatankarp.domain.gateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.hello.openapi.v1_current.HelloApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.openapitools.client.infrastructure.ApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Converter.Factory
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class DomainGatewayConfig(
    @Value("\${hello-service.base-url}")
    private val helloServiceBaseURL: String,
    @Value("\${goodbye-service.base-url}")
    private val goodbyeServiceBaseURL: String,
) {
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
            baseUrl = helloServiceBaseURL,
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
            baseUrl = goodbyeServiceBaseURL,
            serializerBuilder = objectMapper,
            okHttpClientBuilder = okHttpClientBuilder(),
            converterFactories = converterFactories,
        ).createService(GoodbyeApi::class.java)
}
