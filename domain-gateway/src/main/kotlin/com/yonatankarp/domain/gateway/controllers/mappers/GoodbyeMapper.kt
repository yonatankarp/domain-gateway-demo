package com.yonatankarp.domain.gateway.controllers.mappers

import com.yonatankarp.gateway.openapi.v1.models.HelloResponse
import org.springframework.http.ResponseEntity
import retrofit2.Response
import com.yonatankarp.goodbye.openapi.v1_current.models.GoodbyeResponse as GoodbyeServiceResponse

object GoodbyeMapper {
    fun Response<GoodbyeServiceResponse>.toResponse(): ResponseEntity<Any> =
        HelloResponse(value = this.body()?.value ?: "Unknown")
            .let { ResponseEntity.ok(it) }
}
