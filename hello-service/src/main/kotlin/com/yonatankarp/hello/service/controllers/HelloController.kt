package com.yonatankarp.hello.service.controllers

import com.yonatankarp.hello.openapi.v1_current.HelloApi
import com.yonatankarp.hello.openapi.v1_current.models.HelloResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController : HelloApi<HelloResponse> {
    override suspend fun hello(name: String): ResponseEntity<HelloResponse> =
        HelloResponse("Hello, $name!")
            .let { ResponseEntity.ok(it) }
}
