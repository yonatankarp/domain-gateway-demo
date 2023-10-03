package com.yonatankarp.domain.gateway.controllers

import com.yonatankarp.domain.gateway.controllers.mappers.GoodbyeMapper.toResponse
import com.yonatankarp.domain.gateway.controllers.mappers.HelloMapper.toResponse
import com.yonatankarp.gateway.openapi.v1.GatewayApi
import com.yonatankarp.goodbye.openapi.v1_current.GoodbyeApi
import com.yonatankarp.hello.openapi.v1_current.HelloApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DomainGatewayController(
    private val helloApi: HelloApi,
    private val goodbyeApi: GoodbyeApi,
) : GatewayApi<Any> {
    override suspend fun hello(name: String): ResponseEntity<Any> = helloApi.hello(name).toResponse()

    override suspend fun goodbye(name: String): ResponseEntity<Any> = goodbyeApi.goodbye(name).toResponse()
}
