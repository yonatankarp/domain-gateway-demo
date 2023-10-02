package com.yonatankarp.goodbye.service.controllers

import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.goodbye.openapi.v1_current.models.GoodbyeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class GoodbyeController : GoodbyeApi<GoodbyeResponse> {
    override suspend fun goodbye(name: String): ResponseEntity<GoodbyeResponse> =
        GoodbyeResponse(value = "Goodbye $name!")
            .let { ResponseEntity.ok(it) }

}
