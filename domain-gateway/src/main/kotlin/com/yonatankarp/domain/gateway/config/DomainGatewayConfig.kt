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

/**
 * Configuration class for setting up API clients and related components in the
 * Domain Gateway service.
 *
 * This class configures and provides API clients for two external services:
 * Hello Service and Goodbye Service.
 * It also configures Jackson for JSON serialization and deserialization.
 *
 * @param helloServiceBaseURL   The base URL of the Hello Service, injected from
 *                              application properties.
 * @param goodbyeServiceBaseURL The base URL of the Goodbye Service, injected
 *                              from application properties.
 */
@Configuration
class DomainGatewayConfig(
    @Value("\${hello-service.base-url}")
    private val helloServiceBaseURL: String,
    @Value("\${goodbye-service.base-url}")
    private val goodbyeServiceBaseURL: String,
) {
    /**
     * Configures an [OkHttpClient] builder with an [HttpLoggingInterceptor]
     * set to log request and response bodies.
     *
     * @return Configured OkHttpClient builder.
     */
    private fun okHttpClientBuilder() =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = BODY },
            )

    /**
     * Provides an API client for the Hello Service.
     *
     * @param objectMapper The ObjectMapper bean provided by Spring.
     * @return An instance of [HelloApi] created with the specified configurations.
     */
    @Bean
    fun helloApiClient(objectMapper: ObjectMapper): HelloApi =
        ApiClient(
            baseUrl = helloServiceBaseURL,
            serializerBuilder = objectMapper,
            okHttpClientBuilder = okHttpClientBuilder(),
        ).createService(HelloApi::class.java)

    /**
     * Provides an API client for the Goodbye Service.
     *
     * @param objectMapper The ObjectMapper bean provided by Spring.
     * @return An instance of [GoodbyeApi] created with the specified configurations.
     */
    @Bean
    fun goodbyeApiClient(objectMapper: ObjectMapper): GoodbyeApi =
        ApiClient(
            baseUrl = goodbyeServiceBaseURL,
            serializerBuilder = objectMapper,
            okHttpClientBuilder = okHttpClientBuilder(),
        ).createService(GoodbyeApi::class.java)
}
