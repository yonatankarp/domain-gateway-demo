package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.gateway.openapi.v1.models.GoodbyeResponse
import org.springframework.http.ResponseEntity
import retrofit2.Response
import com.yonatankarp.goodbye.openapi.v1_current.models.GoodbyeResponse as GoodbyeServiceResponse

/**
 * An object responsible for mapping responses from the Goodbye Service
 * to HTTP responses suitable for returning from a controller.
 */
object GoodbyeMapper {
    /**
     * Maps a response from the Goodbye Service to an HTTP response entity.
     *
     * @return A ResponseEntity containing the mapped response.
     */
    fun Response<GoodbyeServiceResponse>.toResponse(): ResponseEntity<Any> =
        GoodbyeResponse(value = this.body()?.value ?: "Unknown")
            .let { ResponseEntity.ok(it) }
}
