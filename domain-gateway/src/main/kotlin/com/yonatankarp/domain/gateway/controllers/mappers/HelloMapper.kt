package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.gateway.openapi.v1.models.HelloResponse
import com.yonatankarp.hello.openapi.v1_current.models.HelloResponse as HelloServerResponse
import org.springframework.http.ResponseEntity
import retrofit2.Response

object HelloMapper {
    fun Response<HelloServerResponse>.toResponse(): ResponseEntity<Any> =
        HelloResponse(value = this.body()?.value ?: "Unknown")
            .let { ResponseEntity.ok(it) }
}
