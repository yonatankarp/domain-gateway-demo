package com.yonatankarp.domain.gateway.demo.controllers

import com.yonatankarp.gateway.openapi.v1.GatewayApi
import com.yonatankarp.gateway.openapi.v1.models.GoodbyeResponse
import com.yonatankarp.gateway.openapi.v1.models.HelloResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DomainGatewayController : GatewayApi {

    override fun hello(
        name: String,
    ): ResponseEntity<HelloResponse> = TODO()

    override fun goodbye(
        name: String,
    ): ResponseEntity<GoodbyeResponse> = TODO()
}
