package com.yonatankarp.domain.gateway.demo.controllers

import com.yonatankarp.gateway.openapi.v1.GatewayApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DomainGatewayController : GatewayApi<Any> {

    override fun hello(
        name: String,
    ): ResponseEntity<Any> = TODO()

    override fun goodbye(
        name: String,
    ): ResponseEntity<Any> = TODO()
}
