package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.gateway.openapi.v1.models.HelloResponse
import org.springframework.http.ResponseEntity
import retrofit2.Response
import com.yonatankarp.hello.openapi.v1_current.models.HelloResponse as HelloServerResponse

/**
 * An object responsible for mapping responses from the Hello Server
 * to HTTP responses suitable for returning from a controller.
 */
object HelloMapper {
    /**
     * Maps a response from the Hello Server to an HTTP response entity.
     *
     * @return A ResponseEntity containing the mapped response.
     */
    fun Response<HelloServerResponse>.toResponse(): ResponseEntity<Any> =
        HelloResponse(value = this.body()?.value ?: "Unknown")
            .let { ResponseEntity.ok(it) }
}
